package com.example.firebase

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebase.ui.theme.FirebaseTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val firebaseDatabase = FirebaseDatabase
                        .getInstance("your-firebase-api")
                    Log.d("Firebase", "Firebase Initialized: ${FirebaseDatabase.getInstance()}")
                    val databaseReference = firebaseDatabase.getReference("StudentInfo")

                    val myRef = firebaseDatabase.getReference("message")

                    myRef.setValue("Hello, World!")

                    FirebaseUI(LocalContext.current, databaseReference)

                }
            }
        }
    }
}

@Composable
fun FirebaseUI(context: Context, databaseReference: DatabaseReference){
    val databaseName = remember { mutableStateOf(TextFieldValue())}
    val databaseIndeks = remember { mutableStateOf(TextFieldValue())}

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White),

        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Firebase",
            modifier = Modifier.padding(15.dp),
            style = TextStyle(color = Color.Black, fontSize = 25.sp),
            fontWeight = FontWeight.Bold
        )

        TextField(value = databaseName.value, onValueChange = {databaseName.value = it},
            placeholder = {Text(text = "Wpisz swoje imię")},
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(value = databaseIndeks.value, onValueChange = {databaseIndeks.value = it},
            placeholder = {Text(text = "Wpisz swój numer indeksu")},
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            val data = DataClass(databaseName.value.text, databaseIndeks.value.text)
            databaseReference.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "Data successfully added to Firebase",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to add data to Firebase", Toast.LENGTH_LONG)
                        .show()
                }
        },
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
            )
        {
            Text(text = "Submit", modifier = Modifier.padding(7.dp))
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nameFromFirebase = snapshot.child("name").getValue(String::class.java)
                    val indeksFromFirebase = snapshot.child("indeks").getValue(String::class.java)
                    Toast.makeText(context, "$nameFromFirebase, $indeksFromFirebase", Toast.LENGTH_LONG).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to read data from Firebase", Toast.LENGTH_LONG).show()
                }
            })
        },
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        )
        {
            Text(text = "Get data", modifier = Modifier.padding(7.dp))
        }
    }
}