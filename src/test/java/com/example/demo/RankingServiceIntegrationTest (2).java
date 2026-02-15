package com.example.demo;

import com.example.demo.dto.StudentRankingDTO;
import com.example.demo.service.RankingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RankingServiceIntegrationTest {

    @Autowired
    private RankingService rankingService;

    @Test
    void withDebtsFilterReturnsOnlyDebtors() {
        List<StudentRankingDTO> rows = rankingService.getRanking(
                "SEM1",
                RankingService.RankingScope.STREAM,
                null,
                null,
                RankingService.DebtFilter.WITH_DEBTS,
                RankingService.RankingSortBy.PERFORMANCE,
                "DESC",
                500
        );

        assertFalse(rows.isEmpty());
        for (StudentRankingDTO row : rows) {
            long debtCount = row.getDebtCount() == null ? 0 : row.getDebtCount();
            assertTrue(debtCount > 0);
        }
    }

    @Test
    void withoutDebtsFilterReturnsOnlyDebtFree() {
        List<StudentRankingDTO> rows = rankingService.getRanking(
                "SEM1",
                RankingService.RankingScope.STREAM,
                null,
                null,
                RankingService.DebtFilter.WITHOUT_DEBTS,
                RankingService.RankingSortBy.PERFORMANCE,
                "DESC",
                500
        );

        assertFalse(rows.isEmpty());
        for (StudentRankingDTO row : rows) {
            long debtCount = row.getDebtCount() == null ? 0 : row.getDebtCount();
            assertTrue(debtCount == 0);
        }
    }

    @Test
    void allFilterKeepsDebtFreeBeforeDebtors() {
        List<StudentRankingDTO> rows = rankingService.getRanking(
                "SEM1",
                RankingService.RankingScope.STREAM,
                null,
                null,
                RankingService.DebtFilter.ALL,
                RankingService.RankingSortBy.PERFORMANCE,
                "DESC",
                500
        );

        assertFalse(rows.isEmpty());

        boolean seenDebtor = false;
        boolean seenDebtFree = false;
        for (StudentRankingDTO row : rows) {
            long debtCount = row.getDebtCount() == null ? 0 : row.getDebtCount();
            if (debtCount > 0) {
                seenDebtor = true;
            } else {
                seenDebtFree = true;
                assertFalse(seenDebtor, "Debt-free student found after debtor");
            }
        }

        assertTrue(seenDebtFree);
        assertTrue(seenDebtor);
    }
}

