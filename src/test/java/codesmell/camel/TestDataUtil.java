package codesmell.camel;

public final class TestDataUtil {

    public static String sampleAdvanceShipNoticeInvalidFormat(String bsn02) {
        String data = TestDataUtil.sampleAdvanceShipNotice(bsn02);
        return data.substring(0, data.trim().lastIndexOf("\n"));
    }
    public static String sampleAdvanceShipNotice(String bsn02) {
        return new StringBuilder()
                .append("ISA*01*0000000000*01*0000000000*ZZ*ABC*ZZ*123456789012345*101127*1719*U*00400*000003438*0*P*>")
                .append("\n")
                .append("GS*PO*99*999999999*20101127*1719*1421*X*004010")
                .append("\n")
                .append("ST*856*0001")
                .append("\n")
                .append("BSN*00*").append(bsn02).append("*20101127*2226*0001")
                .append("\n")
                .append("HL*1**S")
                .append("\n")
                .append("TD1**160****G*6256*LB")
                .append("\n")
                .append("TD5**2*WALMRT")
                .append("\n")
                .append("REF*UCB*60504900000438841")
                .append("\n")
                .append("DTM*011*20210329")
                .append("\n")
                .append("FOB*PP")
                .append("\n")
                .append("N1*SF*Sunkist*UL*0")
                .append("N1*ST*WAL-MART GROCERY DC #6057*UL*0")
                .append("\n")
                .append("HL*2*1*O")
                .append("\n")
                .append("PRF*0558834757***20210323")
                .append("\n")
                .append("REF*IA*99")
                .append("\n")
                .append("REF*IV*77")
                .append("\n")
                .append("HL*3*2*P")
                .append("\n")
                .append("MAN*GM*1001001")
                .append("\n")
                .append("HL*4*3*ZZ")
                .append("\n")
                .append("LIN**LT*BBC")
                .append("\n")
                .append("SN1**32*EA")
                .append("\n")
                .append("CTT*3")
                .append("\n")
                .append("SE*23*0001")
                .append("\n")
                .append("GE*1*1421")
                .append("\n")
                .append("IEA*1*000003438")
                .append("\n")
                .toString();
    }
}
