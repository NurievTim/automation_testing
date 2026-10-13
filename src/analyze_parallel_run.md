## Для поддержания параллелизма: 
- SessionStorage стал типа ThreadLocal
- включены настройки junit параллелизма

## Вывод:
> С использованием параллелизма тестов время прогона уменьшилось в **2 раза**

## Сравнение конфигураций запуска

| Конфигурация | Кол-во потоков | Время старта приложения (s) | Время чистого прогона (s) | Общее время (s) |
|---|---|---|---|---|
| Без параллелизации | 1 | 1,182 | 18,619 | 19,801 |
| С параллелизацией | 9 | 1,239 | 9,271 | 10,51 |


## Тесты по тредам (прогон в 9 потоков)

| Thread | Package | Test | Time (ms) |
|---|---|---|---|
| worker-1 | api | UserDepositTest.userCannotDepositToNonExistentAccount() | 2485 |
| worker-1 | api | UserDepositTest.userCannotDepositInadmissibleAmountToSelfAccount(double,String) | 1963 |
| worker-1 | api | UserDepositTest.userCannotDepositInadmissibleAmountToSelfAccount(double,String) | 2215 |
| worker-1 | api | UserTransferTest.userHasNotEnoughAmountToTransfer() | 1945 |
| worker-2 | ui | UserTransferTest.userCannotMakeTransferNotConfirmed() | 2205 |
| worker-2 | ui | UserTransferTest.userCannotMakeTransferWithInvalidAmount() | 2191 |
| worker-3 | ui | UserChangeNameTest.userCannotChangeName() | 1970 |
| worker-3 | ui | UserChangeNameTest.userCanChangeName() | 1714 |
| worker-4 | ui | UserDepositTest.userCanMakeDeposit() | 2108 |
| worker-4 | ui | UserDepositTest.userCannotMakeDeposit() | 1347 |
| worker-4 | api | UserDepositTest.userCanDepositToSelfAccount(double) | 1142 |
| worker-5 | api | UserTransferTest.userCannotTransferInadmissibleAmountBetweenTheirAccounts(double,String) | 2808 |
| worker-5 | api | UserTransferTest.userCannotTransferInadmissibleAmountBetweenTheirAccounts(double,String) | 2507 |
| worker-5 | api | UserTransferTest.userCanTransferToAnotherUser() | 3238 |
| worker-6 | api | UserChangeNameTest.userCannotPutInvalidName(String) | 1304 |
| worker-6 | api | UserChangeNameTest.userCannotPutInvalidName(String) | 1439 |
| worker-6 | api | UserChangeNameTest.userCannotPutInvalidName(String) | 1767 |
| worker-6 | api | UserChangeNameTest.userCannotPutInvalidName(String) | 1313 |
| worker-6 | api | UserChangeNameTest.userCanChangeName() | 1279 |
| worker-6 | api | UserDepositTest.userCanDepositToSelfAccount(double) | 1186 |
| worker-7 | ui | UserTransferTest.userCanMakeTransfer() | 3140 |
| worker-7 | api | UserDepositTest.userCanDepositToSelfAccount(double) | 850 |
| worker-8 | api | UserTransferTest.userCanTransferBetweenTheirAccounts(double) | 2851 |
| worker-9 | api | UserTransferTest.userCanTransferBetweenTheirAccounts(double) | 3441 |
| worker-9 | api | UserTransferTest.userCanTransferBetweenTheirAccounts(double) | 2838 |

