package info.sitnikov.finance.model;

import java.time.LocalDateTime;
import java.util.List;

public interface AmountOperations {
    // Удаление платежа по его идентификатору
    boolean removeAmountById(String amountId);

    // Удаление платежа/платежей по конкретной дате/времени
    boolean removeAmountByTime(LocalDateTime time);

    // Удаление платежей за всё время, с указанной даты по текущую, с указанной даты по указанную дату
    boolean removeAmounts();

    boolean removeAmounts(LocalDateTime from);

    boolean removeAmounts(LocalDateTime from, LocalDateTime to);


    // Получение платежей за всё время, с указанной даты по текущую, с указанной даты по указанную дату
    List<Amount> filterAmounts();

    List<Amount> filterAmounts(LocalDateTime from);

    List<Amount> filterAmounts(LocalDateTime from, LocalDateTime to);


    // Калькуляция дебета и кредита за всё время, с указанной даты по текущую, с указанной даты по указанную дату
    double calculate();

    double calculate(LocalDateTime from);

    double calculate(LocalDateTime from, LocalDateTime to);


    // Подсчёт расходов за всё время, с указанной даты по текущую, с указанной даты по указанную дату
    double expenses();

    double expenses(LocalDateTime from);

    double expenses(LocalDateTime from, LocalDateTime to);


    // Подсчёт доходов за всё время, с указанной даты по текущую, с указанной даты по указанную дату
    double incomes();

    double incomes(LocalDateTime from);

    double incomes(LocalDateTime from, LocalDateTime to);
}
