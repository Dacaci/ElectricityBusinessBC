package com.eb.eb_backend.service;

import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReservationRepository reservationRepository;

    public byte[] generateReservationReceiptPdf(Long reservationId) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée avec l'ID: " + reservationId));

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = page.getMediaBox().getHeight() - 72; // 1 inch margin

                cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
                cs.beginText();
                cs.newLineAtOffset(72, y);
                cs.showText("Reçu de réservation");
                cs.endText();

                y -= 24;
                cs.setFont(PDType1Font.HELVETICA, 12);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                y = writeLine(cs, 72, y, "ID réservation: " + r.getId());
                y = writeLine(cs, 72, y, "Client: " + r.getUser().getFirstName() + " " + r.getUser().getLastName() + " (" + r.getUser().getEmail() + ")");
                y = writeLine(cs, 72, y, "Station: " + r.getStation().getName());
                // Adresse depuis addressEntity de location
                String address = r.getStation().getLocation().getAddressEntity() != null
                        ? r.getStation().getLocation().getAddressEntity().getFullAddress()
                        : "Lat: " + r.getStation().getLocation().getLatitude() + 
                          ", Long: " + r.getStation().getLocation().getLongitude();
                y = writeLine(cs, 72, y, "Adresse: " + address);
                y = writeLine(cs, 72, y, "Période: " + r.getStartTime().format(dtf) + " -> " + r.getEndTime().format(dtf));
                y = writeLine(cs, 72, y, "Durée (h): " + r.getDurationInHours());
                y = writeLine(cs, 72, y, "Tarif horaire: " + r.getStation().getHourlyRate() + " €");
                y = writeLine(cs, 72, y, "Montant total: " + r.getTotalAmount() + " €");
                y = writeLine(cs, 72, y, "Statut: " + r.getStatus());

                y -= 16;
                y = writeLine(cs, 72, y, "Signature propriétaire: __________________________");
            }

            doc.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private float writeLine(PDPageContentStream cs, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - 16;
    }
}













