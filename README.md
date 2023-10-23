<p align = "center">
<img src = "https://github.com/DenisGithuku/Intimo/assets/47632042/4f0e2d5b-126e-46a4-9dd5-99d2b128f440" />
</p>

### About
Intimo (spanish for Personal) serves as a personal Android client for intentional device management and productivity. Employing modern features and practices it provides a fluid and intuitive interface that feels great to use.

### Motivation
After going through other solutions provided out there in the market, I couldn't find a single one of them that suited my requirements. Therefore, I embarked on a journey to create an app that was not only feature rich but also simple and uncluttered. That is when the notion of Intimo struck my mind.

Intimo is powerful and relies on a modern set of APIs to provide a seamless user experience. Some of the features that are already incorporated include:-
1. Device usage dashboard
2. Daily and weekly device usage aggregation
3. Habit history
4. Habit scheduling
5. Habit goals

Some of the planned features include but not limited to:-
1. Device usage limiting
2. Dedicated device and habit widgets
3. Cloud backup.


### Installation
You can get the app directly from [Playstore](https://play.google.com/store/apps/details?id=com.githukudenis.intimo)

To install the app directly from GitHub, the app depends on Kotlin compiler version `1.9.0`, grade version `8.0.0` and compose compiler version `1.5.0`. 
1. First clone the repository onto your local machine.
2. Perform a Gradle sync to download all the dependencies.
3. Run the app and enjoy.


### Tech stack
Intimo relies heavily on the [Jetpack](https://developer.android.com/jetpack) suite of libraries by google. The UI is created using [Jetpack Compose](https://developer.android.com/jetpack/compose).

### Architecture
The app employs the [Recommended Architecture](https://developer.android.com/topic/architecture#recommended-app-arch)  to achieve robustness and clear separation of concerns. 

The app is separated into the following layers:-

<img src = "https://github.com/DenisGithuku/Intimo/assets/47632042/5a2dfdb6-250a-4570-baca-32010972f8f5" height = "80%"  width = "80%"/>

### Modules
<table>
<thead>
<tr>
	<th>Module</th>
	<th>Purpose</th>
</tr>
</thead>
<tbody>
<tr>
	<td>:app</td>
	<td>Serves as the entry point</td>
</tr>
<tr>
	<td>:core:designsystem</td>
	<td>Contains all things design</td>
</tr>
<tr>
	<td>:core:util</td>
	<td>Contains all utilities eg String extension functions</td>
</tr>
<tr>
	<td>:core:ui</td>
	<td>Contains common ui components</td>
</tr>
<tr>
	<td>:core:data</td>
	<td>Combines all the data from the various sources</td>
</tr>
<tr>
	<td>:core:database</td>
	<td>Contains database implementations</td>
</tr>
<tr>
	<td>:core:datastore</td>
	<td>Contains data from the datastore prefs</td>
</tr>
<tr>
	<td>:core:local</td>
	<td>Contains data from disk eg. usage statistics</td>
</tr>
<tr>
	<td>:core:model</td>
	<td>Contains all data model definitions</td>
</tr>
<tr>
	<td>:feauture:onboarding</td>
	<td>Onboarding logic on setup</td>
</tr>
<tr>
	<td>:feature:summary</td>
	<td>Dashboard data on home screen</td>
</tr>
<tr>
	<td>:feature:habit</td>
	<td>Habit logic implementation</td>
</tr>
<tr>
	<td>:feature:settings</td>
	<td>User preferences</td>
</tr>
<tr>
	<td>:feature:usage_stats</td>
	<td>Device usage statistics implementation</td>
</tr>
</tbody>
</table>


### Interface
<div style = "display: flex; flex-direction: row; justify-content: center; flex-wrap: wrap">

<img src = "https://github.com/DenisGithuku/Intimo/assets/47632042/cad02758-a0ec-42ed-b9eb-763f0d82d532" width = "30%" height = "30%" />

<img src = "https://github.com/DenisGithuku/Intimo/assets/47632042/fdf7894e-aed6-4e37-81f3-6f74d6ba7058" width = "30%" height = "30%" />

<img src = "https://github.com/DenisGithuku/Intimo/assets/47632042/3defce48-caab-44f6-9115-33dfe7c0a3fd" width = "30%" height = "30%" />

<img src = "https://github.com/DenisGithuku/Intimo/assets/47632042/a03c213f-ae90-45eb-b78e-ac15f57cd997" width = "30%" height = "30%" />

<img src = "https://github.com/DenisGithuku/Intimo/assets/47632042/29124dad-2695-4a91-96d0-aee4aa796e2f" width = "30%" height = "30%" />

<img src = "https://github.com/DenisGithuku/Intimo/assets/47632042/609fbdca-e055-4be0-b5d5-cc77eb412566" width = "30%" height = "30%" />

<img src = "https://github.com/DenisGithuku/Intimo/assets/47632042/8abd1687-a8ee-4e1d-b694-0484124e76d4" width = "30%" height = "30%" />

</div>
### State of things
The app is in constant development hence new features are added regularly.

Any feedback is welcome through: githukudenis@gmail.com
