#Where's My Disc App

**Summary**: This is a senior design project Android application to help a disc golf player improve their game by tracking the flight path of a disc. This is a full Android Studio project. To get started, clone repository using "Clone in Desktop" button. Then open the project in Android Studio.

**Features**: 
- Connect to disc and transfer flight data over bluetooth. 
- Plot flight path on map of course. 
- Calculate flight statistics. 
- Provide player with best throw and angle adjustments.

**APIs**: 
- Google Maps Android v2
- Google Play Services

**What's Done**:
- Layout for Connect, Data and Map tabs.
- Bluetooth enable, discover devices.
- Buffered data transfer (the device is limited to 20 bytes maximum transfer at a time).
- Method for parsing raw GPS strings.
- General table structure for throw data.
- Methods for inserting into table and querying the data.
- ListView on data tab for throw data (need to update the fields shown).
- Activity for more throw information comes up when a throw is selected.
- Map of current phone location.
- Circle displays maximum throw distance from totals data.
- User can plot points for next throws, will add new circle at new point.
- User can undo throws as needed.
- User can view path of previous throws.
- User can save plotted throws.

**TODO**: 

*Data Storage:*
- Add methods to differentiate between different holes and different games.
- Add IMU class to hold IMU data points.
- Create way of correctly ordering GPS points to IMU points.

*Data Transfer:*


*Calculations:*
- Add methods to calculate throw integrity, initial and final directions.
- Totals data updates.

*Display:*
- Add preferences page in menu.
- Change markers for throw to number of hole.


