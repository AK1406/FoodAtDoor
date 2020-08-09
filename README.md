Complete work of the app (link is given below)

https://drive.google.com/file/d/1Af3pO-sRpjaTzVW0LqTdEKYre3AN9KTH/view?usp=drivesdk


Name of the app : FoodAtDoor

Domain of the project : Android Development

Project Goal : Just a project to apply my android skills in making app based on real life and               
               sharping my skill by learning more and more.

Detailed Description:

 Overview: 

An android app made in kotlin through which users can make their account and enjoy their online delivered food within minutes.
No need to go outside or making dishes in this busy life. Just pay online and enjoy food.

Environmental Used:

Room Library
Volley
Recycler View
Fragments
Kotlin & xml (Language)
Android Studio(IDE)
Custom Api
Firebase Database
Firebase Storage
 
1.Frontend : xml to design layout of each activity and to define constant strings placed in layouts.

2.Backend : Kotlin for working of each activity so that user can interact with app.
                    Firebase Authentication for authenticating users (via email,phone and google signIn)
                    Firebase database for storing user information 
                    Firebase Storage for storing Users ProfilePic.
                    
Dependencies need to be installed :


dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'androidx.core:core-ktx:1.3.1'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    implementation 'com.google.firebase:firebase-auth:19.3.2'
    implementation 'com.google.firebase:firebase-core:17.4.4'
    implementation 'com.google.firebase:firebase-database:19.3.1'
    implementation 'androidx.navigation:navigation-fragment:2.3.0'
    implementation 'androidx.navigation:navigation-ui:2.3.0'
    implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.3.0'
    implementation 'androidx.navigation:navigation-ui-ktx:2.3.0'
    implementation 'com.google.firebase:firebase-firestore:21.5.0'
    implementation 'com.google.firebase:firebase-storage:19.1.1'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
    implementation 'com.google.android.gms:play-services-auth:18.1.0'
    implementation 'com.google.android.material:material:1.2.0'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'com.facebook.android:facebook-android-sdk:5.15.3'

    implementation 'com.squareup.picasso:picasso:2.71828'

    implementation 'com.github.bumptech.glide:glide:4.10.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'

    implementation 'com.hbb20:ccp:2.2.9'
    def nav_version = "2.1.0-alpha04"

    implementation "androidx.navigation:navigation-fragment-ktx:$nav_version" // For Kotlin use navigation-fragment-ktx
    implementation "androidx.navigation:navigation-ui-ktx:$nav_version" // For Kotlin use navigation-ui-ktx
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test:runner:1.2.0'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
    implementation 'de.hdodenhof:circleimageview:2.2.0'
    implementation "androidx.recyclerview:recyclerview:1.1.0"
    
    // For control over item selection of both touch and mouse driven selection
    implementation "androidx.recyclerview:recyclerview-selection:1.1.0-rc01"
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'com.google.android.gms:play-services-location:17.0.0'
    implementation 'com.android.volley:volley:1.1.1'

    // Extensions = ViewModel + LiveData
    
    implementation "android.arch.lifecycle:extensions:1.1.1"
    kapt "android.arch.lifecycle:compiler:1.1.1"
   // Room
   
    implementation "android.arch.persistence.room:runtime:1.1.1"
    kapt "android.arch.persistence.room:compiler:1.1.1"

    //paypal sdk
    implementation 'com.paypal.sdk:paypal-android-sdk:2.15.3'
