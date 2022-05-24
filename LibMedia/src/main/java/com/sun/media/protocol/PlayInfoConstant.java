package com.sun.media.protocol;

public class PlayInfoConstant {

    public enum EncyptedUrlType {

        SIMPLEAES("SimpleAES"),
        WIDEVINE("widevine");

        EncyptedUrlType(String type){
            value = type;
        }

        private  String value;

        public String  getValue(){
            return value;
        }
    }
}
