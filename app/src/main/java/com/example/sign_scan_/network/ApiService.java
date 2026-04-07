package com.example.sign_scan_.network;

import com.example.sign_scan_.models.LoginRequest;
import com.example.sign_scan_.models.LoginResponse;
import com.example.sign_scan_.models.SignupRequest;
import com.example.sign_scan_.models.SignupResponse;
import com.example.sign_scan_.models.DetectionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // 🔐 USER / ADMIN LOGIN
    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // 📝 USER SIGNUP
    @POST("signup/")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    // 🔑 FORGOT PASSWORD
    @POST("forgot-password/")
    Call<okhttp3.ResponseBody> forgotPassword(@Body com.example.sign_scan_.models.ForgotPasswordRequest request);

    // 📸 DETECT SIGN (Multipart)
    @retrofit2.http.Multipart
    @POST("detect-sign/")
    Call<DetectionResponse> detectSign(
            @retrofit2.http.Part okhttp3.MultipartBody.Part image
    );

    // 📜 DETECTION HISTORY
    @retrofit2.http.GET("detection-history/")
    Call<java.util.List<com.example.sign_scan_.models.Detection>> getDetectionHistory();

    // 🗑️ DELETE DETECTION
    @retrofit2.http.DELETE("detection-history/{id}/delete/")
    Call<Void> deleteDetection(@retrofit2.http.Path("id") int id);

    // 🛑 TRAFFIC SIGNS LIBRARY
    @retrofit2.http.GET("signs/")
    Call<java.util.List<com.example.sign_scan_.models.TrafficSign>> getTrafficSigns(@retrofit2.http.Query("category") String category);

    // 👮 TRAFFIC FINES
    @retrofit2.http.GET("traffic-fines/")
    Call<java.util.List<com.example.sign_scan_.models.TrafficFine>> getTrafficFines();

    // 🎓 QUIZ
    @retrofit2.http.POST("quiz/start/")
    Call<com.example.sign_scan_.models.QuizAttemptResponse> startQuiz();

    @retrofit2.http.POST("quiz/answer/")
    Call<okhttp3.ResponseBody> submitAnswer(@retrofit2.http.Body java.util.Map<String, Object> body);

    @retrofit2.http.POST("quiz/finish/")
    Call<okhttp3.ResponseBody> finishQuiz(@retrofit2.http.Body java.util.Map<String, Object> body);

    @retrofit2.http.GET("quiz/review/{attempt_id}/")
    Call<java.util.List<com.example.sign_scan_.models.QuizReviewItem>> getQuizReview(@retrofit2.http.Path("attempt_id") int attemptId);

    // 🛡️ ADMIN
    @retrofit2.http.POST("admin/login/")
    Call<com.example.sign_scan_.models.AdminLoginResponse> adminLogin(@retrofit2.http.Body com.example.sign_scan_.models.AdminLoginRequest request);

    @retrofit2.http.GET("admin/traffic-fines/")
    Call<java.util.List<com.example.sign_scan_.models.TrafficFine>> getAdminTrafficFines();

    @retrofit2.http.POST("admin/traffic-fines/add/")
    Call<okhttp3.ResponseBody> adminAddTrafficFine(@retrofit2.http.Body com.example.sign_scan_.models.TrafficFine fine);

    @retrofit2.http.POST("admin/traffic-fines/{fine_id}/delete/")
    Call<okhttp3.ResponseBody> adminDeleteTrafficFine(@retrofit2.http.Path("fine_id") int fineId);

    // 👤 Admin – User Management
    @retrofit2.http.GET("admin/users/")
    Call<java.util.List<com.example.sign_scan_.models.User>> adminGetUserList();

    @retrofit2.http.POST("admin/users/add/")
    Call<okhttp3.ResponseBody> adminAddUser(@retrofit2.http.Body com.example.sign_scan_.models.User user);

    @retrofit2.http.POST("admin/users/{user_id}/update/")
    Call<okhttp3.ResponseBody> adminUpdateUser(@retrofit2.http.Path("user_id") int userId, @retrofit2.http.Body com.example.sign_scan_.models.User user);

    @retrofit2.http.POST("admin/users/{user_id}/delete/")
    Call<okhttp3.ResponseBody> adminDeleteUser(@retrofit2.http.Path("user_id") int userId);

    // 🔍 Admin – Detector Monitoring
    @retrofit2.http.GET("admin/detections/")
    Call<java.util.List<com.example.sign_scan_.models.Detection>> adminGetDetections();

    @retrofit2.http.POST("admin/detections/{detection_id}/delete/")
    Call<okhttp3.ResponseBody> adminDeleteDetection(@retrofit2.http.Path("detection_id") int detectionId);

    @retrofit2.http.GET("admin/login-activity/")
    Call<java.util.List<com.example.sign_scan_.models.LoginActivity>> getAdminLoginActivityLogs();

    @retrofit2.http.GET("admin/quiz-results/")
    Call<java.util.List<com.example.sign_scan_.models.AdminQuizResult>> getAdminQuizResults();
}
