#!/usr/bin/python3

'''
Copyright (C) 2023 NIBIO

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

@author Tor-Einar Skog <tor-einar.skog@nibio.no>
'''

# Fetches the package registry list from our GitLab,
# filters only models packages,
# figures out which is the newest version of each package,
# creates Maven dependencies XML stubs
# dumps to stdout
# Example usage: 
# $ ./build_models_xml.py -s > models.xml

import sys
import requests
import xml.etree.ElementTree as ET

if len(sys.argv) == 2 and sys.argv[1] == "-h":
    print("""usage: build_with_models [-s]""")
    exit(0)

# Use the -s option to fetch only SNAPSHOT versions
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

model_prefixes = ["no/nibio/vips/model", "fi/luke/vips/model"]

# Filter models and store only the most recent version (highest alphanumeric number)
for package in all_packages:
    for model_prefix in model_prefixes:
        if package["name"].find(model_prefix) >= 0 and ((snapshot and package["version"].find("SNAPSHOT") >= 0) or (not snapshot and package["version"].find("SNAPSHOT") < 0)):
            model_name = package["name"][len(model_prefix) + 1:]
            if models.get(model_name, None) is None:
                models[model_name] = {}
                models[model_name]["groupId"] = model_prefix.replace("/", ".")
                models[model_name]["version"] = package["version"]
            else:
                models[model_name]["version"] = package["version"] if package["version"] > models[model_name]["version"] else models[model_name]["version"]

# Build partial Maven XML
dependencies = ET.Element("dependencies")
for model_name, value in models.items():
    dependency = ET.SubElement(dependencies, "dependency")
    ET.SubElement(dependency, "groupId").text = value["groupId"]
    ET.SubElement(dependency, "artifactId").text = model_name
    ET.SubElement(dependency, "version").text = value["version"]

# Dumps the indented XML to stdout
tree = ET.ElementTree(dependencies)
ET.indent(tree, space="\t", level=0)
ET.dump(tree)
