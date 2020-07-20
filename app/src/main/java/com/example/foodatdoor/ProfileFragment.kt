package com.example.foodatdoor

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"
    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 100
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var myRef: DatabaseReference
    private var userId: String? = null
    private var person: ProfileModel? = null
    private lateinit var edit: ImageView
    private lateinit var delete: Button

    companion object {
        private val TAG = ProfileFragment::class.java.simpleName
        fun newInstance(): Fragment {
            return ProfileFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                UserName.text = person?.name
                UserPhnNo.text = person?.phnNo
                UserEmail.text = person?.email
                UserAddress.text=person?.address
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException())
            }
        })


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            profilePic.setOnClickListener {
                takePictureIntent()
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
                            // Toast.makeText(activity,"Everything is Updated",Toast.LENGTH_LONG).show()
                        } else {
                            //  Toast.makeText(activity,task.exception?.message!!,Toast.LENGTH_LONG).show()
                        }
                    }
            }

            text_not_verified.setOnClickListener {
                user?.sendEmailVerification()
                    ?.addOnCompleteListener {
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

        delete = view.findViewById(R.id.delete)
        delete.setOnClickListener {
            remove(person!!)
        }

    }

    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
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

        nameUpdate.setText(User.name)
        phnUpdate.setText(User.phnNo)
        emailUpdate.setText(User.email)
        addressUpdate.setText(User.address)

        builder.setView(view)

        builder.setPositiveButton("Update", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {

                val upUser = FirebaseDatabase.getInstance().getReference("profile")

                val name = nameUpdate.text.toString().trim()
                val phn = phnUpdate.text.toString()
                val email = emailUpdate.text.toString()
                val address = addressUpdate.text.toString()

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
                    emailUpdate.requestFocus()
                    return
                }


                val person = ProfileModel(User.id, name, phn,address,email)
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
                val email = emailUpdate.text.toString()
                val address = addressUpdate.text.toString()


                val person = ProfileModel(User.id, name, phn,address,email)
                userId = user?.uid
                upUser.child(userId!!).setValue(person)
                Toast.makeText(context, "Information remains as it is", Toast.LENGTH_SHORT).show()
            }


        })

        val alert = builder.create()
        alert.show()

    }


    private fun remove(User: ProfileModel) {
        userId = user?.uid
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Account deleted!")
        builder.setMessage("Really want to delete account ?")
        builder.setPositiveButton("YES"){text,Listener->
            val addedUser = FirebaseDatabase.getInstance().getReference("profile").child(userId!!)
            addedUser.removeValue()
            val user = FirebaseAuth.getInstance().currentUser!!
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context,"Account Deleted !",Toast.LENGTH_LONG).show()
                        val intent =Intent(context,LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
        }
        builder.setNegativeButton("CANCEL"){ text,Listener->
            Toast.makeText(context,"Account Not Deleted !",Toast.LENGTH_LONG).show()
        }
        builder.create()
        builder.show()

    }
}

