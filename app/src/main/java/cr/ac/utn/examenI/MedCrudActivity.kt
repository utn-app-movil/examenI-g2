package cr.ac.utn.examenI

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.examenI.domain.model.MedCheckup
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import cr.ac.utn.examenI.viewmodel.MedViewModel
import java.time.LocalDateTime

class MedCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var txtBloodPressure: EditText
    private lateinit var txtWeight: EditText
    private lateinit var txtHeight: EditText
    private lateinit var txtOxygenation: EditText
    private lateinit var txtDateTime: EditText

    private var currentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_med_crud)
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
        txtBloodPressure = findViewById(R.id.txtBP)
        txtWeight = findViewById(R.id.txtWeight)
        txtHeight = findViewById(R.id.txtHeight)
        txtOxygenation = findViewById(R.id.txtOxygen)
        txtDateTime = findViewById(R.id.txtDateTime)

        txtDateTime.isFocusableInTouchMode = false
        txtDateTime.isFocusable = false
        txtDateTime.setOnClickListener { showDateTimePicker() }

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        } else {
            txtDateTime.setText(util.formatDateTime(LocalDateTime.now()))
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener { doSave() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { doDelete() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
    }

    private fun loadExistingData(id: String) {
        val item = MedViewModel.getById(id)
        if (item == null) {
            showMessage(R.string.TextDataNoFound)
            finish()
            return
        }
        txtId.setText(item.id)
        txtId.isEnabled = false
        txtName.setText(item.name)
        txtFirstName.setText(item.firstName)
        txtLastName.setText(item.lastName)
        txtPhone.setText(if (item.phone == 0) "" else item.phone.toString())
        txtEmail.setText(item.email)
        txtAddress.setText(item.address)
        txtBloodPressure.setText(item.bloodPressure)
        txtWeight.setText(item.weight.toString())
        txtHeight.setText(item.height.toString())
        txtOxygenation.setText(item.oxygenation.toString())
        txtDateTime.setText(util.formatDateTime(item.checkDateTime))
    }

    private fun showDateTimePicker() {
        val current = util.parseStringToDateTimeModern(txtDateTime.text.toString().trim())
            ?: LocalDateTime.now()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        val selected = LocalDateTime.of(year, month + 1, day, hour, minute)
                        txtDateTime.setText(util.formatDateTime(selected))
                    },
                    current.hour,
                    current.minute,
                    true
                ).show()
            },
            current.year,
            current.monthValue - 1,
            current.dayOfMonth
        ).show()
    }

    private fun buildCheckup(): MedCheckup? {
        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val bloodPressure = txtBloodPressure.text.toString().trim()
        val weightText = txtWeight.text.toString().trim()
        val heightText = txtHeight.text.toString().trim()
        val oxygenationText = txtOxygenation.text.toString().trim()
        val dateTimeText = txtDateTime.text.toString().trim()

        if (id.isEmpty() || name.isEmpty() || bloodPressure.isEmpty() || weightText.isEmpty() ||
            heightText.isEmpty() || oxygenationText.isEmpty() || dateTimeText.isEmpty()
        ) {
            return rejectData()
        }

        if (!BLOOD_PRESSURE_REGEX.matches(bloodPressure)) {
            return rejectData()
        }
        val pressures = bloodPressure.split("/").map { it.toInt() }
        if (pressures[0] <= pressures[1]) {
            return rejectData()
        }

        val weight = parseDecimal(weightText)
        if (weight == null || weight <= 0.0) {
            return rejectData()
        }

        val height = parseDecimal(heightText)
        if (height == null || height <= 0.0) {
            return rejectData()
        }

        val oxygenation = parseDecimal(oxygenationText)
        if (oxygenation == null || oxygenation <= 0.0 || oxygenation > 100.0) {
            return rejectData()
        }

        val phoneText = txtPhone.text.toString().trim()
        val phone: Int? = if (phoneText.isEmpty()) 0 else phoneText.toIntOrNull()
        if (phone == null || phone < 0) {
            return rejectData()
        }

        val email = txtEmail.text.toString().trim()
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return rejectData()
        }

        val dateTime = util.parseStringToDateTimeModern(dateTimeText)
        if (dateTime == null) {
            return rejectData()
        }

        return MedCheckup(
            id = id,
            name = name,
            firstName = txtFirstName.text.toString().trim(),
            lastName = txtLastName.text.toString().trim(),
            phone = phone,
            email = email,
            address = txtAddress.text.toString().trim(),
            bloodPressure = bloodPressure,
            weight = weight,
            height = height,
            oxygenation = oxygenation,
            checkDateTime = dateTime
        )
    }

    private fun doSave() {
        val item = buildCheckup() ?: return

        if (currentId.isNullOrEmpty()) {
            if (MedViewModel.getById(item.id) != null) {
                showMessage(R.string.TextDuplicateData)
                return
            }
            MedViewModel.save(item)
        } else {
            MedViewModel.update(item)
        }

        showMessage(R.string.TextSaveSuccess)
        finish()
    }

    private fun doDelete() {
        val id = txtId.text.toString().trim()
        if (id.isEmpty()) return
        if (MedViewModel.getById(id) == null) {
            showMessage(R.string.TextDataNoFound)
            return
        }
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            MedViewModel.delete(id)
            showMessage(R.string.TextDeleteSuccess)
            finish()
        }
    }

    private fun parseDecimal(text: String): Double? = text.replace(',', '.').toDoubleOrNull()

    private fun rejectData(): MedCheckup? {
        showMessage(R.string.TextSaveError)
        return null
    }

    private fun showMessage(messageId: Int) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
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

    private companion object {
        val BLOOD_PRESSURE_REGEX = Regex("""^\d{2,3}/\d{2,3}$""")
    }
}