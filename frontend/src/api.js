export const fetchTopStudents = async () => {
  try {
    const response = await fetch('/api/ranking/top');
    if (!response.ok) {
      throw new Error('Server error');
    }
    const data = await response.json();
    return data;
  } catch (error) {
    return [
      { fullName: "Олександр Петренко", groupName: "КН-31", averageScore: 95.5 },
      { fullName: "Марія Іваненко", groupName: "ІПЗ-31", averageScore: 92.0 },
      { fullName: "Мирослав Третяк", groupName: "ІПЗ-31", averageScore: 88.4 },
    ];
  }
};

export const fetchStudentDashboard = (id) => {
  const isMiroslav = id === "1";

  return {
    name: isMiroslav ? "Третяк Мирослав" : "Олександр Петренко",
    group: isMiroslav ? "ІПЗ-31" : "КН-31",
    gpa: isMiroslav ? 88.4 : 92.1,
    ranking: { stream: isMiroslav ? 12 : 3, total: 120 },
    credits: 30,
    weightedAvg: 89.2,
    cluster: isMiroslav ? "High Performer" : "Top Talent",
    scholarship: true,
    radarData: [
      { subject: 'Math', A: isMiroslav ? 120 : 140, fullMark: 150 },
      { subject: 'Coding', A: 140, fullMark: 150 },
      { subject: 'English', A: 99, fullMark: 150 },
      { subject: 'Soft Skills', A: 86, fullMark: 150 },
      { subject: 'DB', A: 110, fullMark: 150 },
    ],
    trendData: [
      { semester: 'Sem 1', gpa: 75 },
      { semester: 'Sem 2', gpa: 82 },
      { semester: 'Sem 3', gpa: 80 },
      { semester: 'Sem 4', gpa: isMiroslav ? 88 : 92 },
    ],
    subjects: [
      { id: 1, name: "Java Spring Boot", score: isMiroslav ? 92 : 98, avgGroup: 78, category: "Tech", credits: 5 },
      { id: 2, name: "React Frontend", score: 88, avgGroup: 82, category: "Tech", credits: 4 },
      { id: 3, name: "Вища математика", score: isMiroslav ? 45 : 90, avgGroup: 65, category: "Math", credits: 5 },
    ]
  };
};