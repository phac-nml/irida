package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used as a response for encapsulating a SISTR result object
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SistrResult {

	@JsonProperty("qc_status")
	private String qcStatus;

	@JsonProperty("qc_messages")
	private String qcMessages;

	private String serovar;
	@JsonProperty("serovar_antigen")
	private String serovarAntigen;
	@JsonProperty("serovar_cgmlst")
	private String serovarCgmlst;
	private String serogroup;
	private String h1;
	private String h2;
	@JsonProperty("o_antigen")
	private String oAntigen;

	@JsonProperty("cgmlst_ST")
	private Long cgmlstST;
	@JsonProperty("cgmlst_subspecies")
	private String cgmlstSubspecies;
	@JsonProperty("cgmlst_genome_match")
	private String cgmlstGenomeMatch;
	@JsonProperty("cgmlst_distance")
	private Long cgmlstDistance;
	@JsonProperty("cgmlst_matching_alleles")
	private Long cgmlstMatchingAlleles;

	@JsonProperty("mash_subspecies")
	private String mashSubspecies;
	@JsonProperty("mash_serovar")
	private String mashSerovar;
	@JsonProperty("mash_genome")
	private String mashGenome;
	@JsonProperty("mash_distance")
	private Float mashDistance;

	public SistrResult() {
	}

	public SistrResult(String qcStatus, String qcMessages, String serovar, String serovarAntigen, String serovarCgmlst, String serogroup,
			String h1, String h2, String oAntigen, Long cgmlstST, String cgmlstSubspecies, String cgmlstGenomeMatch,
			Long cgmlstDistance, Long cgmlstMatchingAlleles, String mashSubspecies, String mashSerovar, String mashGenome,
			Float mashDistance) {
		this.qcStatus = qcStatus;
		this.qcMessages = qcMessages;
		this.serovar = serovar;
		this.serovarAntigen = serovarAntigen;
		this.serovarCgmlst = serovarCgmlst;
		this.serogroup = serogroup;
		this.h1 = h1;
		this.h2 = h2;
		this.oAntigen = oAntigen;
		this.cgmlstST = cgmlstST;
		this.cgmlstSubspecies = cgmlstSubspecies;
		this.cgmlstGenomeMatch = cgmlstGenomeMatch;
		this.cgmlstDistance = cgmlstDistance;
		this.cgmlstMatchingAlleles = cgmlstMatchingAlleles;
		this.mashSubspecies = mashSubspecies;
		this.mashSerovar = mashSerovar;
		this.mashGenome = mashGenome;
		this.mashDistance = mashDistance;
	}

	public String getQcStatus() {
		return qcStatus;
	}

	public void setQcStatus(String qcStatus) {
		this.qcStatus = qcStatus;
	}

	public String getQcMessages() {
		return qcMessages;
	}

	public void setQcMessages(String qcMessages) {
		this.qcMessages = qcMessages;
	}

	public String getSerovar() {
		return serovar;
	}

	public void setSerovar(String serovar) {
		this.serovar = serovar;
	}

	public String getSerovarAntigen() {
		return serovarAntigen;
	}

	public void setSerovarAntigen(String serovarAntigen) {
		this.serovarAntigen = serovarAntigen;
	}

	public String getSerovarCgmlst() {
		return serovarCgmlst;
	}

	public void setSerovarCgmlst(String serovarCgmlst) {
		this.serovarCgmlst = serovarCgmlst;
	}

	public String getSerogroup() {
		return serogroup;
	}

	public void setSerogroup(String serogroup) {
		this.serogroup = serogroup;
	}

	public String getH1() {
		return h1;
	}

	public void setH1(String h1) {
		this.h1 = h1;
	}

	public String getH2() {
		return h2;
	}

	public void setH2(String h2) {
		this.h2 = h2;
	}

	public String getOAntigen() {
		return oAntigen;
	}

	public void setOAntigen(String oAntigen) {
		this.oAntigen = oAntigen;
	}

	public Long getCgmlstST() {
		return cgmlstST;
	}

	public void setCgmlstST(Long cgmlstST) {
		this.cgmlstST = cgmlstST;
	}

	public String getCgmlstSubspecies() {
		return cgmlstSubspecies;
	}

	public void setCgmlstSubspecies(String cgmlstSubspecies) {
		this.cgmlstSubspecies = cgmlstSubspecies;
	}

	public String getCgmlstGenomeMatch() {
		return cgmlstGenomeMatch;
	}

	public void setCgmlstGenomeMatch(String cgmlstGenomeMatch) {
		this.cgmlstGenomeMatch = cgmlstGenomeMatch;
	}

	public Long getCgmlstDistance() {
		return cgmlstDistance;
	}

	public void setCgmlstDistance(Long cgmlstDistance) {
		this.cgmlstDistance = cgmlstDistance;
	}

	public Long getCgmlstMatchingAlleles() {
		return cgmlstMatchingAlleles;
	}

	public void setCgmlstMatchingAlleles(Long cgmlstMatchingAlleles) {
		this.cgmlstMatchingAlleles = cgmlstMatchingAlleles;
	}

	public String getMashSubspecies() {
		return mashSubspecies;
	}

	public void setMashSubspecies(String mashSubspecies) {
		this.mashSubspecies = mashSubspecies;
	}

	public String getMashSerovar() {
		return mashSerovar;
	}

	public void setMashSerovar(String mashSerovar) {
		this.mashSerovar = mashSerovar;
	}

	public String getMashGenome() {
		return mashGenome;
	}

	public void setMashGenome(String mashGenome) {
		this.mashGenome = mashGenome;
	}

	public Float getMashDistance() {
		return mashDistance;
	}

	public void setMashDistance(Float mashDistance) {
		this.mashDistance = mashDistance;
	}
}
