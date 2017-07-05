 SELECT CANCER_STUDY_ID FROM cbioportal.cancer_study where CANCER_STUDY_IDENTIFIER='acc_tcga_chuv'  ;
SELECT GENETIC_PROFILE_ID,STABLE_ID FROM cbioportal.genetic_profile where CANCER_STUDY_ID=161;
SELECT INTERNAL_ID,STABLE_ID,CANCER_STUDY_ID FROM cbioportal.patient where CANCER_STUDY_ID=161;

SELECT * FROM cbioportal.sample where PATIENT_ID in 
(SELECT INTERNAL_ID FROM cbioportal.patient where CANCER_STUDY_ID=161);

-- empty
SELECT * FROM cbioportal.clinical_sample where INTERNAL_ID in
(SELECT INTERNAL_ID FROM cbioportal.sample where PATIENT_ID in 
(SELECT INTERNAL_ID FROM cbioportal.patient where CANCER_STUDY_ID=161)) ;