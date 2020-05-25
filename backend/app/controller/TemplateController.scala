package controller

import javax.inject.Inject
import repo.TemplateRepo

class TemplateController @Inject() (templateRepo: TemplateRepo, authenticator: Authenticator) extends EntityController(templateRepo, authenticator)
