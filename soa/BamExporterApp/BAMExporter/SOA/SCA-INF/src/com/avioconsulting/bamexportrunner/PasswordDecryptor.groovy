package com.avioconsulting.bamexportrunner

import weblogic.security.internal.SerializedSystemIni
import weblogic.security.internal.encryption.ClearOrEncryptedService

class PasswordDecryptor {
    private final String domainHome

    PasswordDecryptor(String domainHome) {
        this.domainHome = domainHome
    }

    String decrypt(String encrypted) {
        def cryptoService = SerializedSystemIni.getEncryptionService(this.domainHome)
        def ces = new ClearOrEncryptedService(cryptoService)
        ces.decrypt encrypted
    }
}
