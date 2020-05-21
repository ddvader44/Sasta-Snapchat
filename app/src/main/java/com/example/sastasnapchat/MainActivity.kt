package com.example.sastasnapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    var emailText: EditText?=null
    var passwordText: EditText?=null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emailText = findViewById(R.id.emailText)
        passwordText = findViewById(R.id.passText)
        if(mAuth.currentUser !=null)
        {
            log()
        }
    }
    fun login(view: View)
    {
        mAuth.signInWithEmailAndPassword(emailText?.text.toString(), passwordText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    log()
                } else {
                    //Sign Up the user
                    mAuth.createUserWithEmailAndPassword(emailText?.text.toString(), passwordText?.text.toString())
                        .addOnCompleteListener(this){ task ->
                            if(task.isSuccessful)
                            {
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.result?.user!!.uid
                                ).child("email").setValue(emailText?.text.toString())
                                log()
                            }else
                            {
                                Toast.makeText(this,"Login Failed! Try Again.",Toast.LENGTH_SHORT).show()
                            }
                        }
                }


            }
    }
    fun log()
    {
        //Move to next Activity
        val intent = Intent(this,SnapsActivity::class.java)
        startActivity(intent)
    }
}
