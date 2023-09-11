#!/usr/bin/python3

import sys
from xml.dom.minidom import parse, parseString

models_xml_path = "models.xml"

if len(sys.argv) > 1:
    models_xml_path = sys.argv[1]

pom_file = open("pom.xml")
models_file = open(models_xml_path)

pom_dom = parse(pom_file)
models_dom = parse(models_file)

pom_deps_element = pom_dom.getElementsByTagName("dependencies")
model_dep_elements = models_dom.getElementsByTagName("dependency")

for dep in model_dep_elements:
    pom_deps_element.append(dep)

print(pom_dom.toprettyxml())

models_file.close()
pom_file.close()
