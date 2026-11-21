package cr.ac.utn.examenI

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.examenI.domain.model.Identifier
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import java.time.LocalDateTime
import cr.ac.utn.examenI.domain.model.Train
import cr.ac.utn.examenI.viewmodel.TrainViewModel

class TrainCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var chkKotlin: CheckBox
    private lateinit var chkAndroid: CheckBox
    private lateinit var chkGit: CheckBox
    private lateinit var chkScrum: CheckBox
    private lateinit var txtDateTime: EditText

    private var currentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_train_crud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtId = findViewById(R.id.txtId)
        txtName = findViewById(R.id.txtName)
        txtFirstName = findViewById(R.id.txtFirstName)
        txtLastName = findViewById(R.id.txtLastName)
        txtPhone = findViewById(R.id.txtPhone)
        txtEmail = findViewById(R.id.txtEmail)
        txtAddress = findViewById(R.id.txtAddress)
        chkKotlin = findViewById(R.id.chkKotlin)
        chkAndroid = findViewById(R.id.chkAndroid)
        chkGit = findViewById(R.id.chkGit)
        chkScrum = findViewById(R.id.chkScrum)
        txtDateTime = findViewById(R.id.txtDateTime)

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        } else {
            txtDateTime.setText(util.formatDateTime(LocalDateTime.now().plusDays(1)))
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener { doSave() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { doDelete() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
    }


    private fun loadExistingData(id: String) {
        val train_item = TrainViewModel.train_getById(id)

        if (train_item == null) {
            Toast.makeText(this, "No se encontró el registro.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        txtId.setText(train_item.id)
        txtName.setText(train_item.name)
        txtFirstName.setText(train_item.firstName)
        txtLastName.setText(train_item.lastName)
        txtPhone.setText(train_item.phone.toString())
        txtEmail.setText(train_item.email)
        txtAddress.setText(train_item.address)

        chkKotlin.isChecked = train_item.train_courses.contains("Kotlin")
        chkAndroid.isChecked = train_item.train_courses.contains("Android Studio")
        chkGit.isChecked = train_item.train_courses.contains("Git")
        chkScrum.isChecked = train_item.train_courses.contains("Scrum")

        txtDateTime.setText(util.formatDateTime(train_item.train_trainingDate))

        txtId.isEnabled = false
        findViewById<Button>(R.id.btnDelete).isEnabled = true
    }


    private fun doSave() {
        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val dateTimeStr = txtDateTime.text.toString().trim()

        if (id.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos requeridos.", Toast.LENGTH_SHORT).show()
            return
        }

        val train_phone = txtPhone.text.toString().trim().toIntOrNull()

        if (train_phone == null) {
            Toast.makeText(this, "Ingrese un teléfono válido.", Toast.LENGTH_SHORT).show()
            return
        }

        val dt = util.parseStringToDateTimeModern(dateTimeStr)

        if (dt == null || dt.isBefore(LocalDateTime.now())) {
            Toast.makeText(this, "La fecha de capacitación debe ser mayor o igual a la actual.", Toast.LENGTH_SHORT).show()
            return
        }

        val train_courses = mutableListOf<String>()

        if (chkKotlin.isChecked) train_courses.add("Kotlin")
        if (chkAndroid.isChecked) train_courses.add("Android Studio")
        if (chkGit.isChecked) train_courses.add("Git")
        if (chkScrum.isChecked) train_courses.add("Scrum")

        if (train_courses.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un curso.", Toast.LENGTH_SHORT).show()
            return
        }

        val train_item = Train(
            id = id,
            name = name,
            firstName = txtFirstName.text.toString().trim(),
            lastName = txtLastName.text.toString().trim(),
            phone = train_phone,
            email = txtEmail.text.toString().trim(),
            address = txtAddress.text.toString().trim(),
            train_courses = train_courses,
            train_trainingDate = dt
        )

        if (currentId.isNullOrEmpty()) {
            if (TrainViewModel.train_getById(id) != null) {
                Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
                return
            }

            TrainViewModel.train_save(train_item)
        } else {
            TrainViewModel.train_update(train_item)
        }


        Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
        finish()
    }


    private fun doDelete() {
        val id = txtId.text.toString().trim()
        if (id.isEmpty()) return
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            TrainViewModel.train_delete(id)
            Toast.makeText(this, getString(R.string.TextDeleteSuccess), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnu_save -> { doSave(); true }
            R.id.mnu_delete -> { doDelete(); true }
            R.id.mnu_cancel -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
