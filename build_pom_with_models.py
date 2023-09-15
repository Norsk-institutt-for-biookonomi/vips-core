#!/usr/bin/python3

'''
Merges pom.xml and models.xml (default) into pom_with_models.xml and
runs mvn clean install -f pom_with_models.xml

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
import sys
import subprocess
from xml.dom.minidom import parse

# Default
models_xml_path = "models.xml"
merged_pom_filename = "pom_with_models.xml"

# Accept alternative models.xml file
if len(sys.argv) > 1:
    models_xml_path = sys.argv[1]

# Read contents
pom_dom = parse("pom.xml")
try:
    models_dom = parse(models_xml_path)
except FileNotFoundError:
    print("ERROR: File %s not found." % models_xml_path)
    exit(1)

# Merge the dependency elements from models.xml into pom.xml
pom_deps_element = pom_dom.getElementsByTagName("dependencies")[0]
model_dep_elements = models_dom.getElementsByTagName("dependency")

for dep in model_dep_elements:
    pom_deps_element.appendChild(dep)

# Write the modified pom dom to file
pom_with_models_file = open(merged_pom_filename,"w")
pom_dom.writexml(pom_with_models_file)
pom_with_models_file.close()
