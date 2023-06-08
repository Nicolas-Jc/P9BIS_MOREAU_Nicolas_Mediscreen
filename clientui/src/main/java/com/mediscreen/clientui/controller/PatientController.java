package com.mediscreen.clientui.controller;

import com.mediscreen.clientui.beans.PatientBean;
import com.mediscreen.clientui.proxies.NotesProxy;
import com.mediscreen.clientui.proxies.PatientsProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class PatientController {

    @Autowired
    private PatientsProxy patientsProxy;

    @Autowired
    private NotesProxy notesProxy;

    private static final Logger logger = LogManager.getLogger(PatientController.class);


    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    // view Patients List
    @GetMapping("/patients")
    public String showPatientsList(Model model) {
        //get all patients:
        List<PatientBean> patients = patientsProxy.getAllPatients();
        model.addAttribute("patients", patients);
        //get note count per patient:
        //Map<Integer, Integer> mapCountOfNotesPerPatient = notesProxy.getCountOfNotesPerPatient();
        //model.addAttribute("mapcountnote", mapCountOfNotesPerPatient);
        return "patients";
    }

    // view Patient/add
    @GetMapping("/patient/add")
    public String addPatientForm(Model model) {
        model.addAttribute("patient", new PatientBean());
        logger.info("View Patient Add loaded");
        return "patientAdd";
    }

    // Button Add Patient To List
    @PostMapping("/patient/validate")
    public String validate(@Valid @ModelAttribute("patient") PatientBean patientBean,
                           BindingResult result, RedirectAttributes redirAttrs) {

        if (result.hasErrors()) {
            return "patientAdd";
        }

        // Cas Patient dejà en BDD
        if (patientsProxy.checkExistPatient(patientBean).equals(Boolean.TRUE)) {
            redirAttrs.addFlashAttribute("errorCreateMessage",
                    "This patient already exists in the database");
            return "patientAdd";
        }

        if (patientBean.getId() == null) {
            patientsProxy.addPatient(patientBean);
            redirAttrs.addFlashAttribute("successSaveMessage",
                    "Patient successfully added to list");
            return "redirect:/patients";
        }

      /*  if (!result.hasErrors()) {
            patientsProxy.addPatient(patientBean);
            redirAttrs.addFlashAttribute("successSaveMessage",
                    "Patient successfully added to list");
            return "redirect:/patients";
        }*/
        /*if (patientsProxy.checkExistPatient(patientBean).equals(Boolean.TRUE)) {
            redirAttrs.addFlashAttribute("errorDeleteMessage",
                    "This patient already exists in the database");
            logger.error("Error creation Patient");
        }*/
        return "patientAdd";
    }

    @GetMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable("id") Integer id, Model model, RedirectAttributes redirAttrs) {
        try {
           /* if (!patientsProxy.getPatientById(id)) {
                logger.error("Patient {} cannot be found'", id);
                return "redirect:/patients";
            }*/
            patientsProxy.deletePatient(id);
            notesProxy.deleteAllPatientNotes(id);
            model.addAttribute("patients", patientsProxy.getAllPatients());
            redirAttrs.addFlashAttribute("successDeleteMessage", "Patient successfully deleted");
        } catch (Exception e) {
            redirAttrs.addFlashAttribute("errorDeleteMessage", "Error during deletion");
            logger.error("Error to delete \"Patient\" : {}", id);
        }
        return "redirect:/patients";
    }

}
