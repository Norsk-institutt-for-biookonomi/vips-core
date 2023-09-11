# Create a client to run a model

Running a model is a matter of creating a client that is able to send a POST request to a common endpoint, and provide Json in the request body and consume the standard Json that is returned from the model.

The **endpoint for running a model** is:

```
{{vipscore_url}}/models/{{MODEL_ID}}/run
```

**Get the model ID** by simply connecting to

```
{{vipscore_url}}/models/
```

So you must create a model configuration object and pass it in the request body. Each model has its own set of input data. 
To see a **configuration example for a specific model**, query

```
{{vipscore_url}}/models/{{MODEL_ID}}/sampleconfig
```

Here's a sample for the Carrot rust fly temperature model (PSILARTEMP)
**NOTE: Not all models are properly documented. If you find models with missing information, get in touch and we'll 
help you out**

```
{
	"loginInfo":{
		"username":"example",
		"password":"example"
	},
	"modelId":"PSILARTEMP",
	"configParameters":{
		"timeZone": "Europe/Oslo",
		"observations":[
			{
				"timeMeasured": "2015-01-01T00:00:00+02:00",
				"elementMeasurementTypeId":"TM",
				"logIntervalId":2,
				"value":-1.1
			}
		]
	}
}
```

The `"loginInfo"` property is only relevant when running the model through the VIPS Core Manager.
An explanation of the input parameters should be provided for each model from this endpoint:

```
{{vipscore_url}}/models/{{MODEL_ID}}/usage
```

If not, please contact the model provider and notify them.

An example of a valid request to run the Carrot rust fly temperature model is provided in the Postman test suite of the project (`/postman_tests`). Here's an excerpt of the returned result:

```
[
    {
        "validTimeStart": "2020-04-19T22:00:00.000+0000",
        "validTimeEnd": "2020-04-19T22:00:00.000+0000",
        "validGeometry": null,
        "warningStatus": 2,
        "allValues": "{\"WEATHER.TMDD5C\":\"5.78\",\"PSILARTEMP.THRESHOLD_1\":\"260\",\"PSILARTEMP.THRESHOLD_2\":\"360\",\"WEATHER.TMD5C\":\"5.78\",\"WEATHER.TMD\":\"10.78\",\"PSILARTEMP.THRESHOLD_3\":\"560\"}",
        "keys": "[\"WEATHER.TMDD5C\",\"PSILARTEMP.THRESHOLD_1\",\"PSILARTEMP.THRESHOLD_2\",\"WEATHER.TMD5C\",\"WEATHER.TMD\",\"PSILARTEMP.THRESHOLD_3\"]"
    },
    {
        "validTimeStart": "2020-04-20T22:00:00.000+0000",
        "validTimeEnd": "2020-04-20T22:00:00.000+0000",
        "validGeometry": null,
        "warningStatus": 2,
        "allValues": "{\"WEATHER.TMDD5C\":\"12.69\",\"PSILARTEMP.THRESHOLD_1\":\"260\",\"PSILARTEMP.THRESHOLD_2\":\"360\",\"WEATHER.TMD5C\":\"6.91\",\"WEATHER.TMD\":\"11.91\",\"PSILARTEMP.THRESHOLD_3\":\"560\"}",
        "keys": "[\"WEATHER.TMDD5C\",\"PSILARTEMP.THRESHOLD_1\",\"PSILARTEMP.THRESHOLD_2\",\"WEATHER.TMD5C\",\"WEATHER.TMD\",\"PSILARTEMP.THRESHOLD_3\"]"
    },
    {
        "validTimeStart": "2020-04-21T22:00:00.000+0000",
        "validTimeEnd": "2020-04-21T22:00:00.000+0000",
        "validGeometry": null,
        "warningStatus": 2,
        "allValues": "{\"WEATHER.TMDD5C\":\"20.02\",\"PSILARTEMP.THRESHOLD_1\":\"260\",\"PSILARTEMP.THRESHOLD_2\":\"360\",\"WEATHER.TMD5C\":\"7.33\",\"WEATHER.TMD\":\"12.33\",\"PSILARTEMP.THRESHOLD_3\":\"560\"}",
        "keys": "[\"WEATHER.TMDD5C\",\"PSILARTEMP.THRESHOLD_1\",\"PSILARTEMP.THRESHOLD_2\",\"WEATHER.TMD5C\",\"WEATHER.TMD\",\"PSILARTEMP.THRESHOLD_3\"]"
    },
    {
        "validTimeStart": "2020-04-22T22:00:00.000+0000",
        "validTimeEnd": "2020-04-22T22:00:00.000+0000",
        "validGeometry": null,
        "warningStatus": 2,
        "allValues": "{\"WEATHER.TMDD5C\":\"26.24\",\"PSILARTEMP.THRESHOLD_1\":\"260\",\"PSILARTEMP.THRESHOLD_2\":\"360\",\"WEATHER.TMD5C\":\"6.21\",\"WEATHER.TMD\":\"11.21\",\"PSILARTEMP.THRESHOLD_3\":\"560\"}",
        "keys": "[\"WEATHER.TMDD5C\",\"PSILARTEMP.THRESHOLD_1\",\"PSILARTEMP.THRESHOLD_2\",\"WEATHER.TMD5C\",\"WEATHER.TMD\",\"PSILARTEMP.THRESHOLD_3\"]"
    },
    {
        "validTimeStart": "2020-04-23T22:00:00.000+0000",
        "validTimeEnd": "2020-04-23T22:00:00.000+0000",
        "validGeometry": null,
        "warningStatus": 2,
        "allValues": "{\"WEATHER.TMDD5C\":\"30.1\",\"PSILARTEMP.THRESHOLD_1\":\"260\",\"PSILARTEMP.THRESHOLD_2\":\"360\",\"WEATHER.TMD5C\":\"3.86\",\"WEATHER.TMD\":\"8.86\",\"PSILARTEMP.THRESHOLD_3\":\"560\"}",
        "keys": "[\"WEATHER.TMDD5C\",\"PSILARTEMP.THRESHOLD_1\",\"PSILARTEMP.THRESHOLD_2\",\"WEATHER.TMD5C\",\"WEATHER.TMD\",\"PSILARTEMP.THRESHOLD_3\"]"
    }
]
```

Each model has its own output parameters, given in "allValues". They all provide a standardized risk assessmen value, called the warningStatus. Codes are as follows:
* 0 (colored grey) = The model is not applicable at the given time (e.g. out of season)
* 1 (colored blue) = Missing data prevents calculation
* 2 (colored green) = No risk
* 3 (colored yellow) = Possible risk
* 4 (colored red) = High risk

The exact interpretation of the warningStatus is provided by the model, and can be read from this endpoint: 

```
{{vipscore_url}}/models/{{MODEL_ID}}/warningstatusinterpretation
```

Example from the PSILARTEMP model:
<h3>Explanation of the graph</h3>
<p>The graphic presentation shows the progress of the day-degree calculation (the line: &laquo;Accumulated day-degrees with the base temperature of 5 degrees C&raquo;) that is the basis for the green, yellow and red warnings (the warning status in the table). The graph show straight horizontal lines for the threshold values. &laquo;Day-degree threshold for low likelihood of attack&raquo; corresponds to when the warning changes from green to yellow (260 day-degrees). &laquo;Day-degree threshold for high likelihood of attack &raquo; corresponds to when the warning changes from yellow to red (360 day-degrees). &laquo;Day-degree threshold for end of flight period&raquo; corresponds to when the warning changes from red to grey (560 day-degrees) and the flight period for the 1st generation is considered to be over. When the line &laquo; Accumulated day-degrees with the base temperature of 5 degrees C&raquo; intersects one of the lines for the day-degree threshold, the warning will advance to the next level and the color of the warning rectangle will change. The graph also shows the daily mean temperature for the relevant weather station.</p>
<p>The graph is dynamic and the user can choose which parameters that are shown by clicking on the explanation below the graph.</p>
<h3>Explanation of the table</h3>
<p>Green warning indicates that the flight period has not yet begun.</p>
<p>Yellow warning indicates that the flight period is beginning and that flies can be coming into the field.</p>
<p>Red warning indicates peak flight period.</p>
<p>Grey warning indicates that the flight period of the 1st generation is over.</p>
<p>Be aware that in areas with field covers (plastic, single or double non-woven covers, etc.) with early crops the preceding season (either on the current field or neighboring fields), the flight period can start earlier due to higher soil temperature under the covers.</p>
