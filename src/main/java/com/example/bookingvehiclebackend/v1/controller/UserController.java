package com.example.bookingvehiclebackend.v1.controller;

import com.example.bookingvehiclebackend.utils.BaseApiResponse;
import com.example.bookingvehiclebackend.v1.dto.RentalRequest;
import com.example.bookingvehiclebackend.v1.dto.Vehicle;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.BookingVehicleRequest;
import com.example.bookingvehiclebackend.v1.dto.request.ProfileRequest;
import com.example.bookingvehiclebackend.v1.repository.RentalRequestRepository;
import com.example.bookingvehiclebackend.v1.repository.VehicleRepository;
import com.example.bookingvehiclebackend.v1.service.AuthenService;
import com.example.bookingvehiclebackend.v1.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "v1/user", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenService authenService;
    private final RentalRequestRepository rentalRequestRepository;
    private final VehicleRepository vehicleRepository;

    @PostMapping("/bookings")
    public BaseApiResponse<?> bookingVehicle(@RequestBody BookingVehicleRequest request) throws Exception {
        return BaseApiResponse.succeed(userService.bookingVehicle
                (request));
    }
    @GetMapping("/profile")
    public BaseApiResponse<?> profile() {
        return BaseApiResponse.succeed(userService.profile());
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path path = Paths.get("/uploads/images/" + filename);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/change-password")
    public BaseApiResponse<Void> changePassword(@RequestBody AuthenRequest request) {
        userService.changePassword(request);
        return BaseApiResponse.succeed();
    }

    @PostMapping("/reset-password")
    public BaseApiResponse<Void> resetPassword(@RequestParam String token,
                                               @RequestBody AuthenRequest changePasswordRequest,
                                               final HttpServletResponse response) {
        userService.resetPassword(token, response, changePasswordRequest);
        return BaseApiResponse.succeed();
    }

    @GetMapping("/verify-email")
    public BaseApiResponse<Void> verifyEmail(@RequestParam String token,
                                             HttpServletResponse response) throws IOException {
        String successful = userService.verifyEmail(token);
        if ("Successful".equals(successful)) {
            response.sendRedirect("/verification-success.html");
        } else {
            response.sendRedirect("/verification-failed.html");
        }

        return BaseApiResponse.succeed();
    }



    @PutMapping("/profile")
    public BaseApiResponse<?> updateProfile(@RequestBody ProfileRequest profileRequest) throws IOException {
        return BaseApiResponse.succeed(userService.updateProfile(profileRequest));
    }


    @GetMapping("/payment/success")
    public void handlePaymentSuccess(@RequestParam("orderCode") long orderCode, HttpServletResponse response) throws IOException {
        // Tìm rental request dựa trên orderCode
        RentalRequest rr = rentalRequestRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Rental request not found"));
        rr.setPaymentStatus(true);
        // Cập nhật trạng thái xe thành "rented"
        Vehicle v = vehicleRepository.findById(rr.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        v.setStatus("RENTED");
        vehicleRepository.save(v);

        // Chuyển hướng về trang home
        response.sendRedirect("http://localhost:5173/home"); // Thay bằng domain thực tế của frontend
    }

}
