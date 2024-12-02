package com.example.firebase

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

@Composable
fun FirebaseUI(databaseReference: DatabaseReference, userName: String) {
    val context = LocalContext.current
    val databaseName = remember { mutableStateOf(TextFieldValue()) }
    val databaseIndeks = remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome, $userName!",
            modifier = Modifier.padding(16.dp),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )

        TextField(
            value = databaseName.value,
            onValueChange = { databaseName.value = it },
            placeholder = { Text(text = "Enter your name") },
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 15.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = databaseIndeks.value,
            onValueChange = { databaseIndeks.value = it },
            placeholder = { Text(text = "Enter your index number") },
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 15.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val data = DataClass(
                    databaseName.value.text,
                    databaseIndeks.value.text
                )
                submitDataToFirebase(databaseReference, data, context)
            },
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            Text("Submit")
        }

        Button(
            onClick = {
                fetchDataFromFirebase(databaseReference, context)
            },
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            Text("Get Data")
        }
    }
}

fun submitDataToFirebase(
    databaseReference: DatabaseReference,
    data: DataClass,
    context: Context
) {
    databaseReference.setValue(data)
        .addOnSuccessListener {
            Toast.makeText(context, "Data successfully added to Firebase",
                Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to add data to Firebase",
                Toast.LENGTH_LONG).show()
        }
}

fun fetchDataFromFirebase(
    databaseReference: DatabaseReference,
    context: Context
) {
    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val nameFromFirebase = snapshot.child("name").getValue(String::class.java)
            val indeksFromFirebase = snapshot.child("indeks").getValue(String::class.java)

            if (nameFromFirebase != null && indeksFromFirebase != null) {
                Toast.makeText(
                    context,
                    "Name: $nameFromFirebase, Index: $indeksFromFirebase",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, "No data found in Firebase", Toast.LENGTH_LONG).show()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "Failed to fetch data from Firebase",
                Toast.LENGTH_LONG).show()
        }
    })
}