#!/usr/bin/python3

# Fetch package registry list, build models.xml structure
# with the latest version of each model

import sys
import requests
import xml.etree.ElementTree as ET

if len(sys.argv) == 2 and sys.argv[1] == "-h":
    print("""usage: build_with_models [-s]""")
    exit(0)

snapshot = True if len(sys.argv) == 2 and sys.argv[1] == "-s" else False

url = "https://gitlab.nibio.no/api/v4/projects/401/packages?per_page=100&order_by=version&sort=desc"
all_packages = []

response = requests.get(url)

if response.status_code == 200:
    # Get the first page contents
    data = response.json()
    all_packages.extend(data)
    # Iterate the next pages
    while 'next' in response.links:
        next_url = response.links["next"]["url"]
        response = requests.get(next_url)
        if response.status_code == 200:
            data = response.json()
            all_packages.extend(data)


models = {}

model_prefix = "no/nibio/vips/model/"

for package in all_packages:
    if package["name"].find(model_prefix) >=0 and ((snapshot and package["version"].find("SNAPSHOT")>=0) or (not snapshot and package["version"].find("SNAPSHOT")<0)):
        model_name = package["name"][len(model_prefix):]
        if models.get(model_name, None) == None:
            models[model_name] = {}
            models[model_name]["version"] = package["version"]
        else:
            models[model_name]["version"] = package["version"] if package["version"] > models[model_name]["version"] else models[model_name]["version"]

# Build partial Maven XML
dependencies = ET.Element("dependencies")
for model_name, value in models.items():
    dependency = ET.SubElement(dependencies, "dependency")
    ET.SubElement(dependency,"groupId").text = "no.nibio.vips.model"
    ET.SubElement(dependency, "artifactId").text = model_name
    ET.SubElement(dependency, "version").text = value["version"]
    #print("%s %s" % (model_name, value["version"]))

tree = ET.ElementTree(dependencies)
ET.indent(tree, space="\t",level=0)
# Dumps the indented XML to stdout
ET.dump(tree)
