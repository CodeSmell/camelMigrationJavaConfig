package codesmell.util;

import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.StandardX12Document;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.txset.asn856.AsnTransactionSet;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

public final class X12GozerDataExtractionUtil {

    public static AsnTransactionSet extractFirstAsnTransactionSet(StandardX12Document x12Doc) {
        AsnTransactionSet asnTxSet = null;
        if (x12Doc != null) {
            // get the first group
            X12Group group = null;
            List<X12Group> groups = x12Doc.getGroups();
            if (!CollectionUtils.isEmpty(groups)) {
                group = groups.get(0);
            }
            // get the first ASN transaction set from the first group
            if (group != null) {
                List<X12TransactionSet> transactions = group.getTransactions();
                if (!CollectionUtils.isEmpty(transactions)) {
                    X12TransactionSet txSet = transactions.get(0);
                    if (txSet instanceof AsnTransactionSet) {
                        asnTxSet = (AsnTransactionSet) txSet;
                    }
                }
            }
        }
        return asnTxSet;
    }

    public static String extractDocumentNumber(StandardX12Document x12Doc) {
        return Optional.ofNullable(x12Doc)
                .map(X12GozerDataExtractionUtil::extractFirstAsnTransactionSet)
                .map(AsnTransactionSet::getShipmentIdentification)
                .orElse(null);
    }
}
