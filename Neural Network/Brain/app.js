const fs = require('fs')
const bodyParser = require('body-parser')
const express = require("express")
const brain = require("brain.js")

const PORT = "8000"

const app = express()
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))

var arduinoData = [] // queue with data received from the app and read by Arduino
var appData = [] // queue with data received from Arduino and read by the app

/* -------------- NEURAL NETWORK -------------- */

// Neural Network setup and training
const network = new brain.NeuralNetwork({ hiddenLayers: [10] })
const trainingSet = JSON.parse(fs.readFileSync('training_set/dataset.json', 'utf8'))
network.train(trainingSet, {
  iterations: 3000,
  log: true,
  logPeriod: 50,
  errorThresh: 0.001
})

/* -------------- HANDLE REQUESTS -------------- */

// Get requests on http://localhost:8000/classify?arg=[70,800,9000,10000]
app.get("/classify", (req, res) => {
  var classification = network.run(JSON.parse(req.query.arg))
  res.json({ output: classification })
})

app.get("/push-arduino", (req, res) => {
  var data = JSON.parse(req.query.data)
  appData.push(data)

  res.json({ result: "Success" })
})

app.get("/get-arduino", (req, res) => {
  res.json({ output: arduinoData })
})

app.get('/push-app', function(req, res) {
  var data = JSON.parse(req.query.data)
  arduinoData.push(data)

  res.json({ result: "Success" })
});

app.get("/get-app", (req, res) => {
  res.json({ output: appData })
})

// Listen to requests on port 8000
app.listen(PORT, () => {
  console.log("Listening on port " + PORT)
})

// local tunnel: lt --port 8000
