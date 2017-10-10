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
Due to the `Google API` limitations this step can not be done automatically. Following steps must be done:

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
  - name: 'Quantity Buy Sheet Found'             # name of the rule
    constraint:
      data:                                      # data to be used to validate constraint rule
        - row: 6
          column: 1
        - row: 0
          column: 11
      expression: |                              # constraint expression in JS language (data parameter is passed to make validations)
        console.log("JS can be used in Java and will be called with parameters from data[]");
        return true;
    values:                                      # values to be copied from source file
      - start:
          row: 21
          column: 0
        end:
          row: 50                                # optional (may be NULL)
      - start:
          row: 21
          column: 4
        end:
          row: 50
...
... 
```

