package org.simpmc.simppay.util.qrcode.vietqr;

/**
 * @author gianglv24
 */

public class VietQr {

    public static int crc16(final byte[] buffer) {
        int crc = 0xFFFF;

        for (int j = 0; j < buffer.length; j++) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
            crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        return crc;
    }

    private static String getCrc16Valid(String vietQRCode) {
        int crc = crc16(vietQRCode.getBytes());
        String crcCode = Integer.toHexString(crc);

        if (crcCode.length() == 4) {
            return crcCode;
        }
        if (crcCode.length() == 3) {
            return "0" + crcCode;
        }
        if (crcCode.length() == 2) {
            return "00" + crcCode;
        }
        if (crcCode.length() == 1) {
            return "000" + crcCode;
        }
        return crcCode;


    }

    public static String getVietQrNotAmount(String bankid, String bankacc) {
        if (isEmpty(bankid) || isEmpty(bankacc) || bankid.length() < 6 || bankacc.length() < 6 || bankacc.length() > 19) {
            return "";//not valid
        }
        String vietQRCode = "000201010212";
        String dvcntt = "0010A000000727";
        String subBenOrg = "00" + String.format("%02d", bankid.length()) + bankid + "01" + String.format("%02d", bankacc.length()) + bankacc;
        String BenOrg = "01" + String.format("%02d", subBenOrg.length()) + subBenOrg;
        dvcntt += BenOrg + "0208QRIBFTTA";
        vietQRCode += "38" + String.format("%02d", dvcntt.length()) + dvcntt;

        vietQRCode += "6304";
        String crcCode = getCrc16Valid(vietQRCode);

        vietQRCode += crcCode.toUpperCase();

        return vietQRCode;

    }

    public static String getVietQr(String bankid, String bankacc, String amount, String description) {
        if (isEmpty(bankid) || isEmpty(bankacc) || bankid.length() < 6) {
            return "";//not valid
        }
        if (isEmpty(amount) || amount.length() > 13) {
            return getVietQrNotAmount(bankid, bankacc);
        }
        String vietQRCode = "000201010212";
        String dvcntt = "0010A000000727";
        String subBenOrg = "00" + String.format("%02d", bankid.length()) + bankid
                + "01" + String.format("%02d", bankacc.length()) + bankacc;
        String BenOrg = "01" + String.format("%02d", subBenOrg.length()) + subBenOrg;
        dvcntt += BenOrg + "0208QRIBFTTA";
        vietQRCode += "38" + String.format("%02d", dvcntt.length()) + dvcntt;
        vietQRCode += "530370454" + String.format("%02d", amount.length()) + amount + "5802VN";
        if (!isEmpty(description)) {

            String desc = "08" + String.format("%02d", description.length()) + description;
            vietQRCode += "62" + String.format("%02d", desc.length()) + desc;
        }
        vietQRCode += "6304";
        String crcCode = getCrc16Valid(vietQRCode);
        vietQRCode += crcCode.toUpperCase();
        System.out.println(vietQRCode);
        return vietQRCode;
    }

    public static String fm02Leng(int leng) {
        if (leng > 10) {
            return "" + leng;
        }
        return "0" + leng;
    }

    public static boolean isEmpty(final CharSequence s) {
        return length(s) == 0;
    }

    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static void main(String[] args) {
        getVietQr("970438", "0392025310", "100000", "test");
    }
}