package com.eb.eb_backend.service;

import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final ReservationRepository reservationRepository;

    public byte[] exportReservations(LocalDateTime from, LocalDateTime to, Reservation.ReservationStatus status) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = wb.createSheet("Reservations");

            // header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Client");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("Station");
            header.createCell(4).setCellValue("Adresse");
            header.createCell(5).setCellValue("Début");
            header.createCell(6).setCellValue("Fin");
            header.createCell(7).setCellValue("Montant");
            header.createCell(8).setCellValue("Statut");

            int rowIdx = 1;
            int page = 0;
            Page<Reservation> p;
            do {
                p = reservationRepository.findAll(PageRequest.of(page, 200));
                for (Reservation r : p.getContent()) {
                    if (from != null && r.getStartTime().isBefore(from)) continue;
                    if (to != null && r.getEndTime().isAfter(to)) continue;
                    if (status != null && r.getStatus() != status) continue;

                    Row row = sheet.createRow(rowIdx++);
                    int c = 0;
                    row.createCell(c++).setCellValue(r.getId());
                    row.createCell(c++).setCellValue(r.getUser().getFirstName() + " " + r.getUser().getLastName());
                    row.createCell(c++).setCellValue(r.getUser().getEmail());
                    row.createCell(c++).setCellValue(r.getStation().getName());
                    // Adresse construite depuis les coordonnées GPS
                    String address = "Lat: " + r.getStation().getLocation().getLatitude() + 
                                    ", Long: " + r.getStation().getLocation().getLongitude();
                    row.createCell(c++).setCellValue(address);
                    row.createCell(c++).setCellValue(r.getStartTime().toString());
                    row.createCell(c++).setCellValue(r.getEndTime().toString());
                    row.createCell(c++).setCellValue(r.getTotalAmount().doubleValue());
                    row.createCell(c).setCellValue(r.getStatus().name());
                }
                page++;
            } while (!p.isLast());

            for (int i = 0; i <= 8; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération de l'Excel", e);
        }
    }
}













