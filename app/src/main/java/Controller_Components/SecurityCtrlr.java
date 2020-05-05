package Controller_Components;

public class SecurityCtrlr {

    public static String Identity ;
    public static String OrganizationName ;
    public static int CertificateEntries = 0 ;
    public static int SecurityProfile ;
    public static  boolean AdditionalRootCertificateCheck ;

    public static String getIdentity() {
        return Identity;
    }

    public static void setIdentity(String identity) {
        Identity = identity;
    }

    public static String getOrganizationName() {
        return OrganizationName;
    }

    public static void setOrganizationName(String organizationName) {
        OrganizationName = organizationName;
    }

    public static int getCertificateEntries() {
        return CertificateEntries;
    }

    public static void setCertificateEntries(int certificateEntries) {
        CertificateEntries = certificateEntries;
    }

    public static int getSecurityProfile() {
        return SecurityProfile;
    }

    public static void setSecurityProfile(int securityProfile) {
        SecurityProfile = securityProfile;
    }

    public static boolean isAdditionalRootCertificateCheck() {
        return AdditionalRootCertificateCheck;
    }

    public static void setAdditionalRootCertificateCheck(boolean additionalRootCertificateCheck) {
        AdditionalRootCertificateCheck = additionalRootCertificateCheck;
    }
}
