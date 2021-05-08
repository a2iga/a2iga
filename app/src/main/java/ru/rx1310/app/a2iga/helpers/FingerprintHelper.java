// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.helpers;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import ru.rx1310.app.a2iga.A2IGA;

// ! From: https://t.me/VolfsChannel/30

@TargetApi(Build.VERSION_CODES.M)
public abstract class FingerprintHelper {

    private FingerprintManager oFingerprintManager;
    private Cipher oCipher;
    private CancellationSignal oCancellationSignal;
	
	private String keystoreTag = "A2IGATag";

	// Проверяем наличие сканера
    public static boolean canUseFingerprint() {
		
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
		
        A2IGA oA2IGA = A2IGA.get();
        KeyguardManager oKeyguardManager = oA2IGA.getSystemService(KeyguardManager.class);
        FingerprintManager oFingerprintManager = oA2IGA.getSystemService(FingerprintManager.class);
		
        return oKeyguardManager.isKeyguardSecure() 
				&& oFingerprintManager != null 
				&& oFingerprintManager.isHardwareDetected() 
				&& oFingerprintManager.hasEnrolledFingerprints();
				
    }

	/* Мы не можем просто так взять и использовать отпечаток без пароля
	 * Нам необходимо создать хранилище ключей, по которому будет
	 * будет производиться авторизация */
    protected FingerprintHelper() throws Exception {
		
		// Получение контекста
		A2IGA oA2IGA = A2IGA.get();
		
		// Создание экземпляра хранилища ключей
        KeyStore oKeyStore = KeyStore.getInstance("AndroidKeyStore");
		
		// Стучимся к сервису распознавания отпечатков
        oFingerprintManager = oA2IGA.getSystemService(FingerprintManager.class);
		
		// Создание экземпляра шифровальщика
        oCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
									+ KeyProperties.BLOCK_MODE_CBC + "/"
									+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
									
		// Загрузка хранилища ключей
        oKeyStore.load(null);
		
		/* Получаем секретный ключ из загруженного хранилища
		 * если отсутствует - создаём */
        SecretKey oSecretKey = (SecretKey) oKeyStore.getKey(keystoreTag, null);
        if (oSecretKey == null) oSecretKey = generateKey();
        
		// Расшифровка
        try { oCipher.init(Cipher.ENCRYPT_MODE, oSecretKey); } 
		catch (KeyPermanentlyInvalidatedException e) {
			
            // Фикс бага Android Marshmallow
            oSecretKey = generateKey();
            oCipher.init(Cipher.ENCRYPT_MODE, oSecretKey);
			
        }
		
    }

	// Если в процессе авторизации произошла ошибка
    public abstract void onAuthenticationError(int errCode, CharSequence errMessage);

	// Получение статуса авторизации
    public abstract void onAuthenticationHelp(int helpCode, CharSequence helpMessage);

	// Успешная авторизация
    public abstract void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result);
	
	// Ошибка
    public abstract void onAuthenticationFailed();

	// Запуск авторизации
    public void startAuth() {
		
        oCancellationSignal = new CancellationSignal();
		
        FingerprintManager.CryptoObject oCryptoObject = new FingerprintManager.CryptoObject(oCipher);
		
        oFingerprintManager.authenticate(oCryptoObject, oCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
			
			@Override
			public void onAuthenticationError(int errCode, CharSequence errMessage) {
				FingerprintHelper.this.onAuthenticationError(errCode, errMessage);
			}

			@Override
			public void onAuthenticationHelp(int helpCode, CharSequence helpMessage) {
				FingerprintHelper.this.onAuthenticationHelp(helpCode, helpMessage);
			}

			@Override
			public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
				FingerprintHelper.this.onAuthenticationSucceeded(result);
			}

			@Override
			public void onAuthenticationFailed() {
				FingerprintHelper.this.onAuthenticationFailed();
			}
				
		}, null);
			
    }

	// Если пользователь отменил операцию
    public void cancel() {
		
        if (oCancellationSignal != null)
            oCancellationSignal.cancel();
			
    }

	// Создание ключа авторизации
    private SecretKey generateKey() throws Exception {
		
        KeyGenerator oKeyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
		
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
			keystoreTag,
			KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
			.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
			.setUserAuthenticationRequired(true)
			.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            builder.setInvalidatedByBiometricEnrollment(false);
        
        oKeyGen.init(builder.build());
		
        return oKeyGen.generateKey();
		
    }
	
}
