package org.kpmp.upload;

import org.kpmp.dao.InstitutionDemographics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends CrudRepository<InstitutionDemographics, Integer> {

	public InstitutionDemographics findByInstitutionName(String institutionName);

}
