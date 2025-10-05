package com.vn.capstone.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.response.order.OrderItemDTO;
import com.vn.capstone.repository.OrderRepository;
import com.itextpdf.text.Element;

@Service
public class InvoiceService {

    private final OrderRepository orderRepository;

    public InvoiceService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Thêm hàm tiện ích để load font Unicode
    private Font loadFont(float size, int style) throws Exception {
        // Chỉ ghi từ trong resources trở đi
        ClassPathResource fontResource = new ClassPathResource("fonts/Roboto-Italic-VariableFont_wdth,wght.ttf");

        try (InputStream is = fontResource.getInputStream()) {
            BaseFont bf = BaseFont.createFont(
                    fontResource.getFilename(), // tên file
                    BaseFont.IDENTITY_H, // hỗ trợ Unicode (tiếng Việt)
                    BaseFont.EMBEDDED,
                    false,
                    is.readAllBytes(), // load từ InputStream
                    null);
            return new Font(bf, size, style);
        }
    }

    public byte[] generateInvoicePdf(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, baos);
        document.open();

        // Fonts
        Font titleFont = loadFont(16, Font.BOLD);
        Font boldFont = loadFont(12, Font.BOLD);
        Font normalFont = loadFont(12, Font.NORMAL);

        // Header: Shop info + QR code
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[] { 70, 30 });

        // Left cell: Shop info
        PdfPCell shopCell = new PdfPCell();
        shopCell.setBorder(Rectangle.NO_BORDER);
        shopCell.addElement(new Paragraph("LAPTOPSHOP", titleFont));
        shopCell.addElement(new Paragraph("Địa chỉ: Cầu Giấy - Hà Nội", normalFont));
        shopCell.addElement(new Paragraph("Điện thoại: 0123456789", normalFont));
        shopCell.addElement(new Paragraph("Website: laptopshop.demo", normalFont));
        headerTable.addCell(shopCell);

        // Right cell: QR code
        String qrContent = "http://localhost:3000/orders/view/" + order.getId();
        BarcodeQRCode qrCode = new BarcodeQRCode(qrContent, 150, 150, null);
        Image qrImage = qrCode.getImage();
        PdfPCell qrCell = new PdfPCell(qrImage, true);
        qrCell.setBorder(Rectangle.NO_BORDER);
        qrCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        headerTable.addCell(qrCell);

        document.add(headerTable);
        document.add(Chunk.NEWLINE);

        // Chi tiết đơn hàng
        Paragraph detailTitle = new Paragraph("Chi tiết đơn hàng", boldFont);
        document.add(detailTitle);

        PdfPTable itemTable = new PdfPTable(4);
        itemTable.setWidthPercentage(100);
        itemTable.setWidths(new float[] { 10, 50, 20, 20 });
        itemTable.addCell("STT");
        itemTable.addCell("Sản phẩm");
        itemTable.addCell("Số lượng");
        itemTable.addCell("Giá");

        // int i = 1;
        // for (OrderItemDTO item : order.getItems()) {
        // itemTable.addCell(String.valueOf(i++));
        // itemTable.addCell(item.getProductName());
        // itemTable.addCell(String.valueOf(item.getQuantity()));
        // itemTable.addCell(String.valueOf(item.getPrice()));
        // }
        // document.add(itemTable);

        document.add(Chunk.NEWLINE);

        // Thông tin thanh toán
        Paragraph payTitle = new Paragraph("Thông tin thanh toán", boldFont);
        document.add(payTitle);
        document.add(new Paragraph("Tổng giá sản phẩm: " + " VND", normalFont)); // + order.getTotalProductPrice()
        document.add(new Paragraph("Phí vận chuyển: " + " VND", normalFont)); /// order.getShippingFee() +
        DecimalFormat df = new DecimalFormat("#,###");
        document.add(new Paragraph("Tổng tiền: " + df.format(order.getTotalPrice()) + " VND", boldFont));

        document.add(Chunk.NEWLINE);

        // Thông tin đơn hàng + Thông tin mua hàng (dạng 2 cột)
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[] { 50, 50 });

        // Cột trái: thông tin đơn hàng
        PdfPCell orderInfo = new PdfPCell();
        orderInfo.setBorder(Rectangle.BOX);
        orderInfo.addElement(new Paragraph("Thông tin đơn hàng", boldFont));
        orderInfo.addElement(new Paragraph("Mã đơn hàng: #" + order.getTrackingCode(), normalFont)); // order.getCode()
        orderInfo.addElement(new Paragraph("Ngày đặt: " + order.getCreatedAt(), normalFont)); // order.getCreatedDate()
        orderInfo.addElement(new Paragraph("Phương thức thanh toán: " + order.getPaymentMethod(), normalFont));
        orderInfo.addElement(new Paragraph("Phương thức vận chuyển: " + order.getShippingMethod(), normalFont));
        infoTable.addCell(orderInfo);

        // Cột phải: thông tin mua hàng
        PdfPCell buyerInfo = new PdfPCell();
        buyerInfo.setBorder(Rectangle.BOX);
        buyerInfo.addElement(new Paragraph("Thông tin mua hàng", boldFont));
        buyerInfo.addElement(new Paragraph("Tên khách hàng:" + order.getReceiverName(), normalFont));
        buyerInfo.addElement(new Paragraph("Địa chỉ:" + order.getReceiverAddress(), normalFont));
        buyerInfo.addElement(new Paragraph("Điện thoại: " + order.getReceiverPhone(), normalFont));
        infoTable.addCell(buyerInfo);

        document.add(infoTable);

        document.close();
        return baos.toByteArray();
    }

}
