package org.simpmc.simppay.database.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpmc.simppay.SPPlugin;
import org.simpmc.simppay.handler.data.BankAPI;
import org.simpmc.simppay.model.Payment;
import org.simpmc.simppay.service.DatabaseService;
import org.simpmc.simppay.service.PaymentService;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "banking_payments")
public class BankingPayment {

    @DatabaseField(columnName = "payment_id", id = true, dataType = DataType.UUID)
    private UUID paymentID;

    @DatabaseField(columnName = "player_uuid", foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private SPPlayer player;

    @DatabaseField(columnName = "amount", canBeNull = false)
    private double amount;

    @DatabaseField(columnName = "timestamp", canBeNull = false, dataType = DataType.LONG)
    private long timestamp;

    @DatabaseField(columnName = "ref_id")
    private String refID;

    @DatabaseField(columnName = "api_provider", canBeNull = false)
    private BankAPI apiProvider;


    public BankingPayment(Payment payment) {
        this.paymentID = payment.getPaymentID();
        // may cause trouble if null, but player should already be created on join
        this.player = SPPlugin.getService(DatabaseService.class).getPlayerService().findByUuid(payment.getPlayerUUID());
        this.amount = payment.getDetail().getAmount();
        this.refID = payment.getDetail().getRefID();
        this.apiProvider = PaymentService.getBankAPI();
        this.timestamp = Instant.now().toEpochMilli();
    }

}
