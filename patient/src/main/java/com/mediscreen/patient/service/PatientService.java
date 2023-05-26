package com.mediscreen.patient.service;

import com.mediscreen.patient.exception.BadRequestException;
import com.mediscreen.patient.exception.DataAlreadyExistException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.repository.PatientRepository;

import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private static final Logger logger = LogManager.getLogger(PatientService.class);

    private final PatientRepository patientRepository;


    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Optional<Patient> getPatientById(int patientId) throws NotFoundException {

        logger.debug("Service: getPatientById - called");
        Optional<Patient> patient = patientRepository.findById(patientId);
        if (patient.isEmpty())
            throw new NotFoundException("The Id patient : " + patientId + " does not exist");
        return patient;
    }

    public List<Patient> getAllPatients() {

        logger.debug("Service: getAllPatients - called");
        List<Patient> patients = patientRepository.findAll();
        logger.debug("Service: getAllPatients - succes");
        return patients;
    }

    public Patient addPatient(Patient patientToAdd) {
        logger.debug("Service : add Patient - called");

        Boolean patientToCheck = patientRepository.findByLastNameAndFirstNameAndBirthDate(patientToAdd.getLastName(),
                patientToAdd.getFirstName(),
                patientToAdd.getBirthDate());

        if (Boolean.TRUE.equals(patientToCheck)) {
            throw new DataAlreadyExistException("The patient " + patientToAdd.getFirstName()
                    + " / " + patientToAdd.getBirthDate() + " already exists");
        }

        Patient patientToSave = patientRepository.save(patientToAdd);
        logger.debug("Service : addPatient - succes");
        return patientToSave;
    }

    public Patient updatePatient(Patient patient) throws BadRequestException, NotFoundException {

        logger.debug("Service : update Patient - supply");
        if (patient.getId() == null) throw new BadRequestException("Patient id is empty!");

        Patient patientToUpdate = patientRepository.findById(patient.getId()).orElse(null);
        if (patientToUpdate == null) {
            throw new NotFoundException("Patient Id : " + patient.getId() + " cannot be found");
        }

        logger.info("Service : update Patient - check");
        return patientRepository.save(patient);
    }

    public void deletePatient(int patientId) throws NotFoundException {

        logger.debug("Service : delete Patient - supply");

        if (!patientRepository.existsById(patientId))
            throw new NotFoundException("Deletion impossible, patient id : "
                    + patientId + " cannot be found");
        patientRepository.deleteById(patientId);
        logger.info("Service : delete Patient - check");
    }
}
