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
- Bluetooth enable, discover devices (not tested yet).
- General table structure for raw throw data, calculated throw data, general GPS data, general IMU data and totals data (may still need refining).
- Methods for inserting into all tables and querying the data from the tables.
- ListView on data tab for throw data (need to update the fields shown).
- Map of current phone location.
- Circle displays maximum throw distance from totals data.
- User can plot points for next throws, will add new circle at new point.
- User can undo throws as needed.
- User can view path of previous throws.
- User can save plotted throws.

**TODO**: 

*Data Storage:*
- Add method to populate hole and game ids to better categorize throws.
- Add IMU class to hold IMU data points.
- Create way of correctly ordering GPS points to IMU points.

*Data Transfer:*
- Test Bluetooth LE discovery with microcontroller.
- Alter connect tab to only search for our disc.
- Write methods to transfer data and place into tables.

*Calculations:*
- Add statistics methods.
- Totals data updates.

*Display:*
- Add activity for throw data when a row is clicked.
- Add preferences page in menu.
- Change markers for throw to number of hole.


