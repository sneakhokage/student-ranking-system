const DEFAULT_SEMESTER = "SEM1";
const RANKING_CACHE_PREFIX = "ranking_cache_";

const toNumber = (value, fallback = 0) => {
  const n = Number(value);
  return Number.isFinite(n) ? n : fallback;
};

const round2 = (value) => Math.round(toNumber(value) * 100) / 100;

const fetchJson = async (url, options) => {
  const response = await fetch(url, options);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status} for ${url}`);
  }
  return response.json();
};

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

export const fetchTopStudents = async (options = {}) => {
  const {
    semesterId = DEFAULT_SEMESTER,
    scope = "STREAM",
    facultyId,
    formOfStudy,
    debtFilter = "ALL",
    sortBy = "PERFORMANCE",
    direction = "DESC",
    limit = 1000,
  } = options;

  const cacheKey = `${RANKING_CACHE_PREFIX}${semesterId}_${scope}_${facultyId || "all"}_${formOfStudy || "all"}_${debtFilter}_${sortBy}_${direction}_${limit}`;
  const params = new URLSearchParams({
    semesterId,
    scope,
    debtFilter,
    sortBy,
    direction,
    limit: String(limit),
  });
  if (facultyId != null && facultyId !== "") {
    params.set("facultyId", String(facultyId));
  }
  if (formOfStudy) {
    params.set("formOfStudy", formOfStudy);
  }

  try {
    let lastError = null;
    for (let attempt = 0; attempt < 3; attempt += 1) {
      try {
        const data = await fetchJson(`/api/ranking/list?${params.toString()}`);
        localStorage.setItem(cacheKey, JSON.stringify(data));
        return data;
      } catch (error) {
        lastError = error;
        if (attempt < 2) {
          await sleep(400);
        }
      }
    }
    throw lastError || new Error("Ranking fetch failed");
  } catch (error) {
    const cached = localStorage.getItem(cacheKey);
    return cached ? JSON.parse(cached) : [];
  }
};

export const fetchFaculties = async () => {
  try {
    return await fetchJson("/api/faculties");
  } catch (error) {
    return [];
  }
};

export const fetchSemesters = async () => {
  try {
    const page = await fetchJson("/api/semesters?size=100&sort=id,desc");
    return page?.content || [];
  } catch (error) {
    return [];
  }
};

export const fetchStudentsList = async (options = {}) => {
  const { groupPrefix } = options;
  try {
    const page = await fetchJson("/api/students?size=2000&sort=fullName,asc");
    const rows = page?.content || [];
    if (!groupPrefix) {
      return rows;
    }
    return rows.filter((s) => (s.groupName || "").startsWith(groupPrefix));
  } catch (error) {
    return [];
  }
};

export const fetchClassification = async (semesterId = DEFAULT_SEMESTER) => {
  try {
    return await fetchJson(`/api/analytics/classification?semesterId=${semesterId}`);
  } catch (error) {
    return { students: [] };
  }
};

export const fetchStudentDashboard = async (studentId, semesterId = DEFAULT_SEMESTER) => {
  try {
    const id = String(studentId);
    const [student, weighted, risk, trend, comparison, gradesPage, subjectsPage, classification, streamRanking] = await Promise.all([
      fetchJson(`/api/students/${id}`),
      fetchJson(`/api/analytics/weighted-gpa?studentId=${id}&semesterId=${semesterId}`),
      fetchJson(`/api/analytics/risk?studentId=${id}&semesterId=${semesterId}`),
      fetchJson(`/api/analytics/trend?studentId=${id}`),
      fetchJson(`/api/comparison/student?studentId=${id}&semesterId=${semesterId}`),
      fetchJson(`/api/grades?studentId=${id}&semesterId=${semesterId}&size=200`),
      fetchJson(`/api/subjects?size=500`),
      fetchJson(`/api/analytics/classification?semesterId=${semesterId}`),
      fetchJson(`/api/ranking/list?semesterId=${semesterId}&scope=STREAM&sortBy=PERFORMANCE&direction=DESC&limit=5000`),
    ]);

    const subjectMap = new Map((subjectsPage?.content || []).map((s) => [s.id, s]));
    const gradeRows = gradesPage?.content || [];
    const groupAverage = round2(comparison?.groupAverage);

    const subjects = gradeRows.map((g) => {
      const meta = subjectMap.get(g.subjectId) || {};
      return {
        id: g.subjectId,
        name: g.subjectName || meta.name || `Subject ${g.subjectId}`,
        score: toNumber(g.score),
        avgGroup: groupAverage,
        category: meta.category || "N/A",
        credits: toNumber(meta.ects),
      };
    });

    const credits = subjects.reduce((acc, s) => acc + toNumber(s.credits), 0);
    const byCategory = new Map();
    subjects.forEach((s) => {
      if (!byCategory.has(s.category)) {
        byCategory.set(s.category, { sum: 0, count: 0 });
      }
      const row = byCategory.get(s.category);
      row.sum += toNumber(s.score);
      row.count += 1;
    });

    const radarData = Array.from(byCategory.entries()).map(([category, row]) => ({
      subject: category,
      A: round2(row.count === 0 ? 0 : row.sum / row.count),
      fullMark: 100,
    }));

    const trendData = (trend?.points || []).map((p) => ({
      semester: p.semesterId,
      gpa: round2(p.averageScore),
    }));

    const ranked = (streamRanking || []).map((s, idx) => ({ ...s, rank: idx + 1 }));
    const currentRank = ranked.find((s) => String(s.studentId) === id);
    const clusterRows = (classification?.students || []);
    const clusterRow = clusterRows.find((s) => String(s.studentId) === id);
    const cluster = clusterRow?.cluster || "MIDDLE";
    const debtCount = toNumber(risk?.debtCount);

    return {
      name: student?.fullName || `Student ${id}`,
      group: student?.groupName || "N/A",
      gpa: round2(comparison?.studentAverage),
      ranking: {
        stream: currentRank?.rank || 0,
        total: ranked.length,
      },
      credits,
      weightedAvg: round2(weighted?.weightedGpa),
      cluster,
      scholarship: cluster === "TOP" && debtCount === 0,
      radarData,
      trendData,
      subjects,
    };
  } catch (error) {
    return {
      name: "Data unavailable",
      group: "N/A",
      gpa: 0,
      ranking: { stream: 0, total: 0 },
      credits: 0,
      weightedAvg: 0,
      cluster: "N/A",
      scholarship: false,
      radarData: [],
      trendData: [],
      subjects: [],
    };
  }
};
