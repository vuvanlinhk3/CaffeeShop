package com.example.coffeeshop.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.coffeeshop.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.example.coffeeshop.R

data class User(val uid: String = "", val name: String = "", val email: String = "", val avatarUrl: String = "")

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private var isEditingName = false
    private var originalName: String = ""
    private var selectedImageUri: Uri? = null
    private var hasChanges = false

    // Sử dụng ActivityResult API để chọn ảnh
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imgAvatar.setImageURI(it)
            checkForChanges()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("users")
        storage = FirebaseStorage.getInstance()

        // Tải dữ liệu người dùng
        loadUserData()

        // Xử lý nút Back
        binding.backBtn.setOnClickListener {
            finish() // Quay lại màn hình trước (SplashActivity hoặc MainActivity)
        }

        // Xử lý nút Thay đổi ảnh đại diện
        binding.btnChangeAvatar.setOnClickListener {
            pickImageFromGallery()
        }

        // Xử lý nút Chỉnh sửa tên
        binding.btnEditName.setOnClickListener {
            toggleEditName()
        }

        // Theo dõi thay đổi trong trường tên
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                checkForChanges()
            }
        })

        // Xử lý nút Lưu thay đổi
        binding.btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser ?: run {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val uid = currentUser.uid
        dbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    binding.tvUidValue.text = it.uid
                    binding.etName.setText(it.name)
                    binding.tvEmailValue.text = it.email
                    originalName = it.name

                    if (it.avatarUrl.isNotEmpty()) {
                        Glide.with(this@ProfileActivity)
                            .load(it.avatarUrl)
                            .into(binding.imgAvatar)
                    } else {
                        binding.imgAvatar.setImageResource(R.drawable.profile)
                    }
                } ?: run {
                    Toast.makeText(this@ProfileActivity, getString(R.string.load_data_failed, "Không tìm thấy dữ liệu"), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, getString(R.string.load_data_failed, error.message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleEditName() {
        isEditingName = !isEditingName
        binding.etName.isEnabled = isEditingName
        if (isEditingName) {
            binding.etName.requestFocus()
            binding.btnEditName.setImageResource(R.drawable.outline_add_task_24) // Thay bằng drawable của bạn
        } else {
            binding.btnEditName.setImageResource(R.drawable.outline_border_color_24) // Thay bằng drawable của bạn
        }
        checkForChanges()
    }

    private fun checkForChanges() {
        val currentName = binding.etName.text.toString().trim()
        hasChanges = currentName != originalName || selectedImageUri != null
        binding.btnSave.isEnabled = hasChanges
        binding.btnSave.alpha = if (hasChanges) 1f else 0.5f
    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun saveChanges() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Không tìm thấy UID", Toast.LENGTH_SHORT).show()
            return
        }
        val newName = binding.etName.text.toString().trim()

        if (newName.isEmpty()) {
            binding.etName.error = "Tên không được để trống"
            return
        }

        if (selectedImageUri != null) {
            val avatarRef = storage.reference.child("avatars/$uid.jpg")
            avatarRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    avatarRef.downloadUrl.addOnSuccessListener { uri ->
                        updateUserInDatabase(uid, newName, uri.toString())
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, getString(R.string.update_failed, e.message), Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, getString(R.string.update_failed, e.message), Toast.LENGTH_SHORT).show()
                }
        } else {
            updateUserInDatabase(uid, newName, null)
        }
    }

    private fun updateUserInDatabase(uid: String, newName: String, avatarUrl: String?) {
        val userUpdates = mutableMapOf<String, Any>()
        userUpdates["name"] = newName
        if (avatarUrl != null) {
            userUpdates["avatarUrl"] = avatarUrl
        }

        dbRef.child(uid).updateChildren(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                originalName = newName
                selectedImageUri = null
                toggleEditName()
                checkForChanges()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, getString(R.string.update_failed, e.message), Toast.LENGTH_SHORT).show()
            }
    }
}