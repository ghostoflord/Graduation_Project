package com.vn.capstone.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import com.vn.capstone.domain.Order;
import com.vn.capstone.repository.OrderRepository;

@Service
public class InvoiceService {

    private final OrderRepository orderRepository;

    public InvoiceService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public byte[] generateInvoicePdf(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = new Font(FontFamily.HELVETICA, 18, Font.BOLD);
        document.add(new Paragraph("PAYMENT INVOICE", titleFont));
        document.add(new Paragraph("Order ID: " + order.getId()));
        document.add(new Paragraph("Customer: " + order.getReceiverName()));
        document.add(new Paragraph("Phone: " + order.getReceiverPhone()));
        document.add(new Paragraph("Total Amount: " + order.getTotalPrice() + " VND"));

        document.add(Chunk.NEWLINE);

        String qrContent = "http://localhost:3000/orders/view/" + order.getId();
        BarcodeQRCode qrCode = new BarcodeQRCode(qrContent, 150, 150, null);
        Image qrImage = qrCode.getImage();
        qrImage.setAlignment(Image.ALIGN_RIGHT);
        document.add(new Paragraph("Scan the QR code to view invoice online:"));
        document.add(qrImage);

        document.close();
        return baos.toByteArray();
    }
}
