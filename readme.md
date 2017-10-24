# JAEX application
JAEX application is Java based application used to merge to files (.xls, .xlxs, .csv) into one without any changes in resulting file.

## Configuration
All configuration options are defined in `application.yml`. To run application with external parameters add following runtime parameter 
```
java -Dspring.config.location=file:///d:/application.yml -jar target/excel-api.jar
```

###Security

Security mechanism is implemented using `Basic` with `stateless` session management (on each new request authorization header must be provided). To define authorized username and password used following configuration
```yaml
security:
  user:
    name: 'XXX'           # authorized username
    password: 'YYY'       # authorized password
```

###Google OAuth

To get access to the `Google Drive` client security token must be created and application must be authorized.
To create and export `client security token` use following instruction:

1. Create or select a project in the Google Developers Console and automatically turn on the API. Click **Continue**, then **Go to credentials**.
2. On the **Add credentials to your project** page, click the **Cancel** button.
3. At the top of the page, select the **OAuth consent screen** tab. Select an **Email address**, enter a **Product name** if not already set, and click the **Save** button.
4. Select the **Credentials** tab, click the **Create credentials** button and select **OAuth client ID**.
5. Select the application type **Other**, enter the name `JAEX Google Drive`, and click the **Create** button.
6. Click **OK** to dismiss the resulting dialog.
7. Click the **Download JSON** button to the right of the client ID.

To authorize application following steps must be done:
Due to the `Google API` limitations this step can not be done automatically. Following steps must be done locally:

1. Build application `mvn clean package`
2. Save exported `client security token` in application root directory under the name `client_secret.json`
2. Run application `java -jar target/excel-api.jar` 
3. While starting will see following warning messages in console:
    ```text
    [WARN] Unable to read stored authorization token from [client_token_store]
    [WARN] This application wasn't authorized to access Google Drive. Please use this url to create credentials token: [XXXX]
    ```
4. Open provided URL in your browser (there will be callback to Your machine from `Google`)
5. Authorization token will be stored in `client_token_store` directory

Now these two parameters can be used to access `Google Drive` without any extra actions.
To configure paths to them use following configuration:
```yaml
google:
  api:
    client_security_path: 'client_secret.json'  # client security token
    client_token_store: 'client_token_store'    # authorization data
```
###Mapping configuration

Mapping configuration is defined in specific structure to make dynamic configuration;
```yaml
rules:
  - name: 'Town Pump'   # name of the rule
    constraint:
      data:
        - row: 0        # A1
          column: 0
        - row: 0        #F1
          column: 5     
        - row: 0        #H1
          column: 7
        - row: 0        #M1
          column: 12  
      expression: |
        return data[0] =='VIN' && data[1] == 'Item UPC' && data[2] == 'Current Everyday Cost' && data[3] == 'Promo Cost'
    values:
      - start:        #I1
          row: 1
          column: 8
      - start:        #J
          row: 1      
          column: 9
      - start:        #K
          row: 1
          column: 10
      - start:        #L
          row: 1
          column: 11
      - start:        #M
          row: 1
          column: 12
      - start:        #N
          row: 1
          column: 13
...
...
```
This rule states:
```text 
if 
    A1 == 'VIN' and
    F1 ==  'Item UPC' and 
    H1 == 'Current Everyday Cost' and
    M1 == 'Promo Cost'
then copy from 
    I1 cell to the end of column,
    J1 cell to the end of column,
    K1 cell to the end of column,
    L1 cell to the end of column,
    M1 cell to the end of column,
    N1 cell to the end of column.
```
Rule itself (`expression`) is a JavaScript statement which must return `true` or `false`;
###File readers

####CSV file reader
To configure CSV file value separator use following configuration (default is ","):
```yaml
csv:
  separator: ','
```

###Profiles
There is preconfigured `Heroku` profile. To run application in `Heroku` just link your repository with `Heroku` account and make a deploy. Running process parameters are defined in `Procfile`:
```text
web: java $JAVA_OPTS -Dspring.profiles.active=heroku -Dserver.port=$PORT -jar target/excel-api.jar
```
###Api URL's
About files. There is 2 urls: 
`/v1/merge-files` - it merges and saves file in google drive. On success it return 204 (no any data returned). 
`/v1/merge-files-instant` - it just merges files and returns zipped result. 

