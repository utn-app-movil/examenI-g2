package cr.ac.utn.examenI

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.examenI.domain.model.Person
import cr.ac.utn.examenI.notif.NotifController
import cr.ac.utn.examenI.notif.NotifEmail
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import java.time.LocalDate
import java.time.LocalDateTime

class NotifCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var txtEmailTitle: EditText
    private lateinit var txtMessage: EditText
    private lateinit var txtCC: EditText
    private lateinit var txtCCO: EditText
    private lateinit var llRecipients: LinearLayout
    private lateinit var txtSendDateTime: EditText
    private lateinit var btnDelete: Button

    // One checkbox per registered person
    private val recipientChecks = mutableListOf<CheckBox>()

    private var currentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notif_crud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notif_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtId = findViewById(R.id.notif_txtId)
        txtName = findViewById(R.id.notif_txtName)
        txtFirstName = findViewById(R.id.notif_txtFirstName)
        txtLastName = findViewById(R.id.notif_txtLastName)
        txtPhone = findViewById(R.id.notif_txtPhone)
        txtEmail = findViewById(R.id.notif_txtEmail)
        txtAddress = findViewById(R.id.notif_txtAddress)
        txtEmailTitle = findViewById(R.id.notif_txtEmailTitle)
        txtMessage = findViewById(R.id.notif_txtMessage)
        txtCC = findViewById(R.id.notif_txtCC)
        txtCCO = findViewById(R.id.notif_txtCCO)
        llRecipients = findViewById(R.id.notif_llRecipients)
        txtSendDateTime = findViewById(R.id.notif_txtSendDateTime)
        btnDelete = findViewById(R.id.notif_btnDelete)

        loadRecipientChecks(emptyList())

        // When leaving the id field, recover the person if it is already registered
        txtId.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) fillSender()
        }

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        } else {
            btnDelete.visibility = View.GONE
            txtSendDateTime.setText(util.formatDateTime(LocalDateTime.now().plusDays(1)))
        }

        findViewById<Button>(R.id.notif_btnSave).setOnClickListener { doSave() }
        btnDelete.setOnClickListener { doDelete() }
        findViewById<Button>(R.id.notif_btnCancel).setOnClickListener { finish() }
    }

    // Builds one checkbox per registered person, keeping the given ids checked
    private fun loadRecipientChecks(selectedIds: List<String>) {
        llRecipients.removeAllViews()
        recipientChecks.clear()
        for (person in NotifController.getPeople()) {
            val check = CheckBox(this)
            check.text = person.fullName
            check.tag = person.id
            check.isChecked = selectedIds.contains(person.id)
            llRecipients.addView(check)
            recipientChecks.add(check)
        }
    }

    private fun getSelectedIds(): List<String> =
        recipientChecks.filter { it.isChecked }.map { it.tag as String }

    // If the typed id is already registered, recover its data
    private fun fillSender() {
        val person = NotifController.getPersonById(txtId.text.toString().trim()) ?: return
        txtName.setText(person.name)
        txtFirstName.setText(person.firstName)
        txtLastName.setText(person.lastName)
        txtPhone.setText(person.phone.toString())
        txtEmail.setText(person.email)
        txtAddress.setText(person.address)
    }

    private fun loadExistingData(id: String) {
        val item = NotifController.getById(id)
        if (item == null) {
            Toast.makeText(this, getString(R.string.TextDataNoFound), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        txtId.setText(item.sender.id)
        txtName.setText(item.sender.name)
        txtFirstName.setText(item.sender.firstName)
        txtLastName.setText(item.sender.lastName)
        txtPhone.setText(item.sender.phone.toString())
        txtEmail.setText(item.sender.email)
        txtAddress.setText(item.sender.address)
        txtEmailTitle.setText(item.title)
        txtMessage.setText(item.message)
        txtCC.setText(item.cc)
        txtCCO.setText(item.cco)
        txtSendDateTime.setText(util.formatDateTime(item.sendDateTime))
        loadRecipientChecks(item.recipients.map { it.id })
    }

    private fun doSave() {
        val senderId = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val title = txtEmailTitle.text.toString().trim()
        val msg = txtMessage.text.toString().trim()
        val dateTimeStr = txtSendDateTime.text.toString().trim()

        if (senderId.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        // Register (or update) the sender as a person and refresh the recipients list
        val sender = Person(
            id = senderId,
            name = name,
            firstName = txtFirstName.text.toString().trim(),
            lastName = txtLastName.text.toString().trim(),
            phone = txtPhone.text.toString().trim().toIntOrNull() ?: 0,
            email = txtEmail.text.toString().trim(),
            address = txtAddress.text.toString().trim()
        )
        val selectedIds = getSelectedIds()
        NotifController.savePerson(sender)
        loadRecipientChecks(selectedIds)

        if (title.isEmpty() || msg.isEmpty()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        // Send date must be after the current day
        val dt = util.parseStringToDateTimeModern(dateTimeStr)
        if (dt == null || !dt.toLocalDate().isAfter(LocalDate.now())) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        // Selected recipients come from the registered people
        val targets = selectedIds.mapNotNull { NotifController.getPersonById(it) }
        if (targets.isEmpty()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val item = NotifEmail(
            id = currentId ?: NotifController.nextId(),
            sender = sender,
            title = title,
            message = msg,
            sendDateTime = dt,
            cc = txtCC.text.toString().trim(),
            cco = txtCCO.text.toString().trim(),
            recipients = targets
        )

        if (NotifController.isDuplicate(item, currentId)) {
            Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
            return
        }

        if (currentId.isNullOrEmpty()) {
            NotifController.add(item)
        } else {
            NotifController.update(item)
        }

        Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun doDelete() {
        val id = currentId
        if (id.isNullOrEmpty()) return
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            NotifController.delete(id)
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
