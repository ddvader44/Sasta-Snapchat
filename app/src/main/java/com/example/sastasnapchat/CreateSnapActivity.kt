package com.example.sastasnapchat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*


class CreateSnapActivity : AppCompatActivity() {
    var createSnapImageView:ImageView?=null
    var messageEditText:EditText?=null
    val imageName = UUID.randomUUID().toString()+".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        createSnapImageView = findViewById(R.id.imageView)
        messageEditText = findViewById(R.id.editText)
    }
    fun chooseImageClicked(view: View)
    {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setTitle("Choose an Option")
            .setMessage("Select an Image from?")
            .setPositiveButton(
                "Camera"
            ) { dialog, which ->
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            }
            .setNegativeButton("Gallery"){dialog, which ->
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 1)
            }
            .show()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImage = data!!.data
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {
                try {
                    val photo = data.extras!!["data"] as Bitmap?
                    createSnapImageView?.setImageBitmap(photo)
                }catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
            1 -> if (resultCode == Activity.RESULT_OK) {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
                    createSnapImageView?.setImageBitmap(bitmap)
                }catch (e:Exception)
                {
                    e.printStackTrace()
                }

            }
        }
    }
    fun nextButtonClicked(view: View)
    {
        // Get the data from an ImageView as bytes
        createSnapImageView?.isDrawingCacheEnabled = true
        createSnapImageView?.buildDrawingCache()
        val bitmap = (createSnapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName)
            .putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this,"Upload Failed! Check your Internet and Try Again!",Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {taskSnapshot ->
            val downloadUrl = taskSnapshot.getMetadata()?.getReference()?.getDownloadUrl().toString()

            val intent = Intent(this,WhoToSendActivity::class.java)
            intent.putExtra("imageURL",downloadUrl)
            intent.putExtra("imageName",imageName)
            intent.putExtra("message",messageEditText?.text.toString())
            startActivity(intent)
        }

    }
    }

