package com.example.foodatdoor

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"
    private lateinit var imageUri: Uri
    private val IMAGE_CAPTURE_CODE = 101
    private val PERMISSION_CODE = 1000
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var myRef: DatabaseReference
    private var userId: String? = null
    private var person: ProfileModel? = null
    private lateinit var edit: ImageView
    private lateinit var pic:ImageView

    private lateinit var U_name:TextView
    private lateinit var U_phnNo:TextView
    private lateinit var U_address:TextView
    private lateinit var U_pinCode:TextView
    private lateinit var U_email:TextView

    companion object {
        private val TAG = ProfileFragment::class.java.simpleName
        fun newInstance(): Fragment {
            return ProfileFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        U_name =view.findViewById(R.id.UserName)
        U_phnNo=view.findViewById(R.id.UserPhnNo)
        U_address=view.findViewById(R.id.UserAddress)
        U_pinCode=view.findViewById(R.id.UserPinCode)
        U_email=view.findViewById(R.id.UserEmail)
        // get reference to 'profile' node
        myRef = FirebaseDatabase.getInstance().getReference("profile")
        // add it only if it is not saved to database
        userId = user?.uid
        // User data change listener
        myRef.child(userId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                person = dataSnapshot.getValue(ProfileModel::class.java)
                // Check for null
                if (person == null) {
                    Log.e(TAG, "User data is null!")
                    return
                }
                U_name.text = person?.name
                U_phnNo.text = person?.phnNo
                U_address.text=person?.address
                U_pinCode.text=person?.pinCode
                U_email.text = person?.email
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException())
            }
        })
        pic=view.findViewById(R.id.profilePic)
        user?.let { user ->
            Glide.with(this)
                .load(user.photoUrl)
                .into(profilePic)

            /**Email verification **/
            if (user.isEmailVerified) {
                text_not_verified.visibility = View.INVISIBLE
                text_verified.visibility = View.VISIBLE
            } else {
                text_not_verified.visibility = View.VISIBLE
                text_verified.visibility = View.INVISIBLE
            }

            pic.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                //set title for alert dialog
                builder.setTitle(R.string.dialogTitle)
                //set message for alert dialog
                builder.setMessage(R.string.dialogMessage)
                builder.setPositiveButton("Camera"){dialogInterface, which ->
                    takePictureIntent()
                }
                //performing cancel action
                builder.setNeutralButton("Cancel"){dialogInterface , which ->
                }
                //performing negative action
                builder.setNegativeButton("Gallery"){dialogInterface, which ->
                     pickPictureIntent()
                }

                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
                alertDialog.show()

            }

            update.setOnClickListener {

                val photo = when {
                    ::imageUri.isInitialized -> imageUri
                    user.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                    else -> user.photoUrl
                }

                val updates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(photo)
                    .build()

                //progressbar.visibility = View.VISIBLE
                user.updateProfile(updates)
                    .addOnCompleteListener { task ->
                        // progressbar.visibility = View.INVISIBLE
                        if (task.isSuccessful) {
                           //  Toast.makeText(activity,"Profile Pic is Updated",Toast.LENGTH_LONG).show()
                        }
                    }
            }

            text_not_verified.setOnClickListener {
                user.sendEmailVerification()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(activity, "Verification email sent ", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, "Failed to send verification email ", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

       /* UserPhnNo.setOnClickListener {
            val intent = Intent(activity, VerifyPhoneFragment::class.java)
            startActivity(intent)
        }
       */ /*
        UserPhnNo.setOnClickListener {
            val action = ProfileFragmentDirections.actionVerifyPhone()
            Navigation.findNavController(it).navigate(action)
        }*/
        edit = view.findViewById(R.id.edit_info)
        edit_info.setOnClickListener {
            updateInfo(person!!)
        }
    }

    private fun takePictureIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                context?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                requestPermissions(permission,PERMISSION_CODE)
            } else {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
                    pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                        startActivityForResult(pictureIntent,IMAGE_CAPTURE_CODE)

                    }
                }
            }
        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
                pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                    startActivityForResult(pictureIntent,IMAGE_CAPTURE_CODE)

                }
            }
        }

    }

    private fun pickPictureIntent(){

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }   else if (requestCode == 12 && resultCode == Activity.RESULT_OK && data != null) {
            val imageuri = data.data
            profilePic.setImageURI(imageuri)
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageuri)
                uploadImageAndSaveUri(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)

        progressbar_pic.visibility = View.VISIBLE
        upload.addOnCompleteListener { uploadTask ->
            progressbar_pic.visibility = View.INVISIBLE

            if (uploadTask.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        //   Toast.makeText(activity,imageUri.toString(),Toast.LENGTH_LONG).show()
                        profilePic.setImageBitmap(bitmap)
                    }
                }
            } else {
                uploadTask.exception?.let {
                    // Toast.makeText(activity,it.message!!,Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun updateInfo(User: ProfileModel) {
        val builder = AlertDialog.Builder(context)
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        builder.setTitle("Update Information")

        val view: View = layoutInflater.inflate(R.layout.update_info, null)

        val nameUpdate: EditText = view.findViewById(R.id.update_name)
        val phnUpdate: EditText = view.findViewById(R.id.update_phn)
        val emailUpdate: EditText = view.findViewById(R.id.update_email)
        val addressUpdate:EditText=view.findViewById(R.id.update_address)
        val areaCodeUpdate:EditText=view.findViewById(R.id.update_pin)

        nameUpdate.setText(User.name)
        phnUpdate.setText(User.phnNo)
        emailUpdate.setText(User.email)
        addressUpdate.setText(User.address)
        areaCodeUpdate.setText(User.pinCode)

        builder.setView(view)

        builder.setPositiveButton("Update", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {

                val upUser = FirebaseDatabase.getInstance().getReference("profile")

                val name = nameUpdate.text.toString().trim()
                val phn = phnUpdate.text.toString()
                val address = addressUpdate.text.toString()
                val areaPin=areaCodeUpdate.text.toString()
                val email = emailUpdate.text.toString()

                if (name.isEmpty()) {
                    nameUpdate.error = " Please enter a name"
                    nameUpdate.requestFocus()
                    return
                }
                if (phn.isEmpty()) {
                    phnUpdate.error = " Please enter Phn no."
                    phnUpdate.requestFocus()
                    return
                }

                if (email.isEmpty()) {
                    emailUpdate.error = " Please enter email"
                    emailUpdate.requestFocus()
                    return
                }


                if (address.isEmpty()) {
                    emailUpdate.error = " Please enter Address"
                    addressUpdate.requestFocus()
                    return
                }

                if (areaPin.isEmpty()) {
                    emailUpdate.error = " Please enter pin Code"
                    areaCodeUpdate.requestFocus()
                    return
                }


                val person = ProfileModel(User.id, name, phn,address,areaPin,email)
                userId = user?.uid
                upUser.child(userId!!).setValue(person)
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
            }

        })
        builder.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {

                val upUser = FirebaseDatabase.getInstance().getReference("profile")

                val name = nameUpdate.text.toString().trim()
                val phn = phnUpdate.text.toString()
                val address = addressUpdate.text.toString()
                val areaPin=areaCodeUpdate.text.toString()
                val email = emailUpdate.text.toString()


                val person = ProfileModel(User.id, name, phn,address,areaPin,email)
                userId = user?.uid
                upUser.child(userId!!).setValue(person)
                Toast.makeText(context, "Information remains as it is", Toast.LENGTH_SHORT).show()
            }

        })

        val alert = builder.create()
        alert.show()
    }

}

