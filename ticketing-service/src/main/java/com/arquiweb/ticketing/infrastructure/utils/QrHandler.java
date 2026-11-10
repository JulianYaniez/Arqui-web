package com.arquiweb.ticketing.infrastructure.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class QrHandler {

    public static String generateQrBase64(String content) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", output);
        byte[] byteImage = output.toByteArray();

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(byteImage);
    }

}
