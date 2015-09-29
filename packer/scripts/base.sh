sed -i "s/^.*requiretty/#Defaults requiretty/" /etc/sudoers

mv /etc/issue /etc/issue.old
cat > /etc/issue <<EOF
░▀█▀░█▀▄░▀█▀░█▀▄░█▀█
░░█░░█▀▄░░█░░█░█░█▀█
░▀▀▀░▀░▀░▀▀▀░▀▀░░▀░▀

Welcome to the IRIDA Virtual Environment.

As you can see, the Virtual Environment does not have a GUI installed. You should interact with IRIDA using your web browser, terminal, or uploader tool.

You can access the IRIDA web interface by navigating to http://localhost:48888/irida in your web browser. The default username and password is \`admin\` and \`password1\`.

ADVANCED USERS ONLY: You can access the IRIDA-managed instance of Galaxy by navigating to http://localhost:49999 in your web browser. You can log into this virtual environment by using the username \`vagrant\` and the password \`vagrant\`. Alternatively, you can SSH into this virtual environment by running \`ssh -p42222 vagrant@localhost\` and using the password \`vagrant\`.

EOF

cat /etc/issue.old >> /etc/issue
rm /etc/issue.old
