    package com.vn.capstone.controller;

    import org.springframework.http.HttpHeaders;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import com.vn.capstone.service.InvoiceService;

    @RestController
    @RequestMapping("/api/v1")
    public class InvoiceController {

        private final InvoiceService invoiceService;

        public InvoiceController(InvoiceService invoiceService) {
            this.invoiceService = invoiceService;
        }

        @GetMapping("/invoice/{orderId}")
        public ResponseEntity<byte[]> generateInvoice(@PathVariable Long orderId) throws Exception {
            byte[] pdfBytes = invoiceService.generateInvoicePdf(orderId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "invoice-" + orderId + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        }
    }
