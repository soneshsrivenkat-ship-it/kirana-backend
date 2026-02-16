package com.example.demo.reporting.repo;

import com.example.demo.reporting.document.FinancialReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface FinancialReportRepository
        extends MongoRepository<FinancialReport, String> {

    Optional<FinancialReport> findByUserIdAndPeriodTypeAndPeriodAndCurrencyType(
            String userId,
            String periodType,
            String period,
            String currencyType
    );
    Optional<FinancialReport> findByUserIdAndPeriod(
            String userId,
            String period
    );
}
