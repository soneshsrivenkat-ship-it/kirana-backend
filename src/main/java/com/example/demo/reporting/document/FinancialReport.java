package com.example.demo.reporting.document;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "financial_reports")
public class FinancialReport {

    @Id
    private String id;
    private String userId;
    private String currencyType;
    private String periodType;
    private String period;
    private BigDecimal totalCredits = BigDecimal.ZERO;
    private BigDecimal totalDebits = BigDecimal.ZERO;
    private BigDecimal netFlow = BigDecimal.ZERO;
    private LocalDateTime lastUpdated;

}

