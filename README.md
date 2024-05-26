This project fetches latest article from Wikipedia using it's own API.
Libraries:
-> Jwiki: To work with MediaWiki API
-> OkHTTP: For secure network connections
-> Glide and Picasso: For Image Loading
-> Basic implementation of S.O.L.I.D techniques and LiveData, DiffUtils and ViewModels
<- UPDATE ->:
Some bugs are fixed:
1. I fixed JavaIllegalStateException which caused by the renaming files and incorrect view usage
2. Then, another bugs come to play with me, 
3. I implemented LiveData and DiffUtils to load data faster and do not disturb main thread. 
4. But, application doesn't show any Wiki Articles as excepted. 
5. I am investigating with this problem.