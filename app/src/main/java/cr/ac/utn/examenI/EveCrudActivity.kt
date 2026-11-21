package cr.ac.utn.examenI

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import java.time.LocalDateTime
import cr.ac.utn.examenI.domain.model.EveEvent
import cr.ac.utn.examenI.viewmodel.EveViewModel

class EveCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var txtInstitution: EditText
    private lateinit var txtVenue: EditText
    private lateinit var txtSeatNum: EditText
    private lateinit var spnEventType: Spinner
    private lateinit var txtDateTime: EditText

    private var currentId: String? = null
    private val eventTypes = arrayOf("Teatro", "Cine", "Concierto")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eve_crud)
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
        txtInstitution = findViewById(R.id.txtInstitution)
        txtVenue = findViewById(R.id.txtVenue)
        txtSeatNum = findViewById(R.id.txtSeatNum)
        spnEventType = findViewById(R.id.spnEventType)
        txtDateTime = findViewById(R.id.txtDateTime)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnEventType.adapter = adapter

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
        val item = EveViewModel.getById(id) ?: return
        txtId.setText(item.id)
        txtId.isEnabled = false
        txtName.setText(item.name)
        txtFirstName.setText(item.firstName)
        txtLastName.setText(item.lastName)
        txtPhone.setText(item.phone.toString())
        txtEmail.setText(item.email)
        txtAddress.setText(item.address)
        txtInstitution.setText(item.institution)
        txtVenue.setText(item.venue)
        txtSeatNum.setText(item.seatNumber)
        val pos = eventTypes.indexOf(item.eventType)
        if (pos >= 0) {
            spnEventType.setSelection(pos)
        }
        txtDateTime.setText(
            util.formatDateTime(item.eventDateTime)
        )
    }
    private fun doSave() {
        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val firstName = txtFirstName.text.toString().trim()
        val lastName = txtLastName.text.toString().trim()
        val phone = txtPhone.text.toString().trim().toIntOrNull()
        val email = txtEmail.text.toString().trim()
        val address = txtAddress.text.toString().trim()
        val institution = txtInstitution.text.toString().trim()
        val venue = txtVenue.text.toString().trim()
        val seat = txtSeatNum.text.toString().trim()
        val eventType = spnEventType.selectedItem.toString()

        val dt = util.parseStringToDateTimeModern(
            txtDateTime.text.toString().trim()
        )

        if (id.isEmpty() || name.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phone == null || email.isEmpty() ||
            address.isEmpty() || institution.isEmpty() || venue.isEmpty() || seat.isEmpty() || dt == null ||
            seat.toIntOrNull() == null || seat.toInt() <= 0
        ) {
            Toast.makeText(
                this,
                getString(R.string.TextSaveError),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val existingWithSeat = EveViewModel.getAll().any {
            it.venue.equals(venue, ignoreCase = true) &&
                    it.eventType == eventType &&
                    it.eventDateTime == dt &&
                    it.seatNumber == seat &&
                    it.id != id
        }

        if (existingWithSeat) {
            Toast.makeText(
                this,
                getString(R.string.TextDuplicateData),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val event = EveEvent(
            id = id,
            name = name,
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            address = address,
            institution = institution,
            venue = venue,
            seatNumber = seat,
            eventType = eventType,
            eventDateTime = dt
        )

        if (currentId.isNullOrEmpty()) {
            if (EveViewModel.getById(id) != null) {
                Toast.makeText(
                    this,
                    getString(R.string.TextDuplicateData),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        util.showDialogCondition(
            this,
            getString(R.string.TextSaveQuestion)
        ) {
            if (currentId.isNullOrEmpty()) {
                EveViewModel.save(event)
            } else {
                EveViewModel.update(event)
            }

            Toast.makeText(
                this,
                getString(R.string.TextSaveSuccess),
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }

    private fun doDelete() {
        val id = txtId.text.toString().trim()

        if (id.isEmpty()) return

        util.showDialogCondition(
            this,
            getString(R.string.TextDeleteQuestion)
        ) {
            EveViewModel.delete(id)

            Toast.makeText(
                this,
                getString(R.string.TextDeleteSuccess),
                Toast.LENGTH_SHORT
            ).show()

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
