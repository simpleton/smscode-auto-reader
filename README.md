# smscode-auto-reader
Help you to read sms code automatically.
## How to User

###1. Include SMS read permission
    
add`<uses-permission android:name="android.permission.READ_SMS" />` in your AndroidManifest.xml file


###2. Register the observer in your code

Put the below codes to your onCreate callback

    smsObserver = new SmsContentObserver(
        new Handler(), 
        YOURACTIVITY.this, 
        new String[]{"[your_tag_name]", [your_other_tag_name]},
        smsCode -> {
            if (StringUtil.isPresent(smsCode)) {
                smscodeEt.setText(smsCode);
            }
        }
    );
    
###3. Unregister the observer in your code

Put the below codes to your onDestory callback

    if (smsObserver != null) {
        smsObserver.unregisterSMSObserver();
        smsObserver = null;
    }
