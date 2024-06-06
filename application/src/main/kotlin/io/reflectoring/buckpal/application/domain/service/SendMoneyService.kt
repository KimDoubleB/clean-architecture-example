package io.reflectoring.buckpal.application.domain.service

import io.reflectoring.buckpal.application.port.`in`.SendMoneyCommand
import io.reflectoring.buckpal.application.port.`in`.SendMoneyUseCase
import io.reflectoring.buckpal.application.port.out.AccountLock
import io.reflectoring.buckpal.application.port.out.LoadAccountPort
import io.reflectoring.buckpal.application.port.out.UpdateAccountStatePort
import io.reflectoring.buckpal.common.UseCase
import lombok.RequiredArgsConstructor
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@RequiredArgsConstructor
@UseCase
@Transactional
internal class SendMoneyService(
    private val loadAccountPort: LoadAccountPort,
    private val accountLock: AccountLock,
    private val updateAccountStatePort: UpdateAccountStatePort,
    private val moneyTransferProperties: MoneyTransferProperties
) : SendMoneyUseCase {

    override fun sendMoney(command: SendMoneyCommand): Boolean {
        checkThreshold(command)

        val baselineDate = LocalDateTime.now().minusDays(10)

        val sourceAccount = loadAccountPort!!.loadAccount(
            command.sourceAccountId,
            baselineDate
        )

        val targetAccount = loadAccountPort.loadAccount(
            command.targetAccountId,
            baselineDate
        )

        val sourceAccountId = sourceAccount.id
            .orElseThrow {
                IllegalStateException(
                    "expected source account ID not to be empty"
                )
            }
        val targetAccountId = targetAccount.id
            .orElseThrow {
                IllegalStateException(
                    "expected target account ID not to be empty"
                )
            }

        accountLock!!.lockAccount(sourceAccountId)
        if (!sourceAccount.withdraw(command.money, targetAccountId)) {
            accountLock.releaseAccount(sourceAccountId)
            return false
        }

        accountLock.lockAccount(targetAccountId)
        if (!targetAccount.deposit(command.money, sourceAccountId)) {
            accountLock.releaseAccount(sourceAccountId)
            accountLock.releaseAccount(targetAccountId)
            return false
        }

        updateAccountStatePort!!.updateActivities(sourceAccount)
        updateAccountStatePort.updateActivities(targetAccount)

        accountLock.releaseAccount(sourceAccountId)
        accountLock.releaseAccount(targetAccountId)
        return true
    }

    private fun checkThreshold(command: SendMoneyCommand) {

    }
}