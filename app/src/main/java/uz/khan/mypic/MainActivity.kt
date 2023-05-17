package uz.khan.mypic

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import uz.khan.mypic.databinding.ActivityMainBinding
import java.net.URI

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebasefirestore: FirebaseFirestore
    private lateinit  var storageRef: StorageReference
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setImageResource(R.drawable.baseline_add_a_photo_24)

        firebasefirestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference("Image")

        binding.imageView.setOnClickListener {
            resultLauncher.launch("image/*")
        }


        binding.upload.setOnClickListener {

            funUpload()
        }


    }

    private fun funUpload() {
        binding.progressBar.visibility = View.VISIBLE
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let { it ->
            storageRef.putFile(it).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnCompleteListener { uri ->


                        val map = HashMap<String, Any>()
                        map["ImageMap"] = uri.toString()
                        firebasefirestore.collection("image").add(map)
                            .addOnCompleteListener { taskIt ->
                                if (taskIt.isSuccessful) {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(this, "Upload image", Toast.LENGTH_SHORT).show()
                                } else {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(this, taskIt.exception?.message, Toast.LENGTH_SHORT).show()
                                }


                            }
                         binding.progressBar.visibility = View.GONE
                        binding.imageView.setImageResource(R.drawable.baseline_add_a_photo_24)


                    }


                } else {

                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }


        }


    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {

        imageUri = it
        binding.imageView.setImageURI(it)

    }


}