package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.sign_scan_.network.RetrofitClient;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.sign_scan_.models.User;

public class ManageUsersActivity extends AppCompatActivity {

    private static final int ADD_USER_REQUEST_CODE = 201;
    private LinearLayout usersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        usersContainer = findViewById(R.id.usersContainer);

        // Back Button Navigation
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        fetchUsers();

        // Setup Add User FAB
        FloatingActionButton fabAddUser = findViewById(R.id.fabAddUser);
        if (fabAddUser != null) {
            fabAddUser.setOnClickListener(v -> {
                Intent intent = new Intent(ManageUsersActivity.this, AddUserActivity.class);
                startActivityForResult(intent, ADD_USER_REQUEST_CODE);
            });
        }
    }

    private void fetchUsers() {
        RetrofitClient.getApiService().adminGetUserList().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderUserList(response.body());
                } else {
                    Toast.makeText(ManageUsersActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ManageUsersActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderUserList(List<User> users) {
        usersContainer.removeAllViews();
        for (User user : users) {
            addUserCardToLayout(user);
        }
    }

    private void addUserCardToLayout(User user) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_user_card, usersContainer, false);

        TextView tvName = cardView.findViewById(R.id.tvUserName);
        TextView tvEmail = cardView.findViewById(R.id.tvUserEmail);
        TextView tvRole = cardView.findViewById(R.id.tvUserRole);
        TextView btnEdit = cardView.findViewById(R.id.btnEditDynamic);
        TextView btnDelete = cardView.findViewById(R.id.btnDeleteDynamic);

        tvName.setText(user.getFullName());
        tvEmail.setText(user.getEmail());
        tvRole.setText("Role: " + user.getRole());

        btnEdit.setOnClickListener(v -> showEditUserDialog(user, tvName, tvEmail, tvRole));
        btnDelete.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser(user.getId(), cardView))
                .setNegativeButton("Cancel", null)
                .show();
        });

        usersContainer.addView(cardView);
    }

    private void deleteUser(int userId, View cardView) {
        RetrofitClient.getApiService().adminDeleteUser(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    usersContainer.removeView(cardView);
                    Toast.makeText(ManageUsersActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageUsersActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ManageUsersActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchUsers(); // Refresh list after adding
        }
    }

    private void showEditUserDialog(User user, TextView nameTarget, TextView emailTarget, TextView roleTarget) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_edit_user, null);
        bottomSheetDialog.setContentView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        Spinner spinnerRole = dialogView.findViewById(R.id.spinnerRole);
        ImageView btnClose = dialogView.findViewById(R.id.btnClose);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSaveChanges);

        etName.setText(user.getFullName());
        etEmail.setText(user.getEmail());

        String[] roles = {"user", "admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        String currentRole = user.getRole().toLowerCase();
        if (currentRole.equals("admin")) spinnerRole.setSelection(1);
        else spinnerRole.setSelection(0);

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newRole = spinnerRole.getSelectedItem().toString();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            User updatedUser = new User(newName, newEmail, newRole, null);
            updatedUser.setId(user.getId());

            RetrofitClient.getApiService().adminUpdateUser(user.getId(), updatedUser).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        nameTarget.setText(newName);
                        emailTarget.setText(newEmail);
                        roleTarget.setText("Role: " + newRole);
                        user.setFullName(newName);
                        user.setEmail(newEmail);
                        user.setRole(newRole);
                        Toast.makeText(ManageUsersActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    } else {
                        String errorMsg = "Update failed: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                errorMsg += " - " + response.errorBody().string();
                            }
                        } catch (Exception e) {}
                        Toast.makeText(ManageUsersActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(ManageUsersActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        bottomSheetDialog.show();
    }
}
